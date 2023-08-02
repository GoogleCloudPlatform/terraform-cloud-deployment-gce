/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecodesamples.cloud.jss.lds.service;

import com.googlecodesamples.cloud.jss.lds.model.BaseFile;
import com.googlecodesamples.cloud.jss.lds.model.FileMeta;
import com.googlecodesamples.cloud.jss.lds.repository.AppConfigRepository;
import com.googlecodesamples.cloud.jss.lds.repository.FileMetaRepository;
import com.googlecodesamples.cloud.jss.lds.util.LdsUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Backend service controller for Cloud SQL and CloudStorage
 */
@Service
public class FileService {

  private static final Logger logger = LoggerFactory.getLogger(FileService.class);

  private static final int THUMBNAIL_SIZE = 300;

  private final FileMetaRepository fileMetaRepo;

  private final AppConfigRepository appConfigRepo;

  private final StorageService storageService;

  @Value("${resource.path}")
  private String basePath;

  @Value("${storage.bucket.name}")
  private String bucketName;

  @Value("${data.init.app.name}")
  private String appName;

  public FileService(FileMetaRepository fileMetaRepo, AppConfigRepository appConfigRepo,
                     StorageService storageService) {
    this.fileMetaRepo = fileMetaRepo;
    this.appConfigRepo = appConfigRepo;
    this.storageService = storageService;
  }

  /**
   * Upload files to Cloud SQL and Cloud Storage.
   *
   * @param files list of files upload to the server
   * @param tags  list of tags label the files
   * @return list of uploaded files
   * @throws IOException if the file cannot be read
   */
  public List<BaseFile> uploadFiles(List<MultipartFile> files, List<String> tags)
      throws IOException {
    logger.info("entering uploadFiles()");
    List<BaseFile> fileList = new ArrayList<>();

    for (MultipartFile file : files) {
      String fileId = LdsUtil.generateUuid();
      BaseFile newFile = createOrUpdateFile(file, tags, fileId, fileId, null);
      fileList.add(newFile);
    }
    return fileList;
  }

  /**
   * Import files to Cloud SQL and Cloud Storage.
   *
   * @param files new file upload to the server
   * @param tags  list of tags label the new file
   * @throws IOException if the file cannot be read
   */
  @Transactional
  public void importFiles(List<File> files, List<String> tags) throws IOException {
    for (File file : files) {
      if (file.isDirectory()) {
        logger.info("skipping directory: {}", file.getName());
        continue;
      }
      String fileId = LdsUtil.generateUuid();
      String name = file.getName();
      long size = file.length();

      BaseFile newFile = createOrUpdateFileMeta(tags, fileId, fileId, name, size, new Date());
      String contentType = Files.probeContentType(file.toPath());
      byte[] content = Files.readAllBytes(file.toPath());
      logger.info("importing file: {}", name);
      saveToGcs(newFile, contentType, content);

      if (newFile.checkImageFileType()) {
        logger.info("generating thumbnail for: {}", name);
        createThumbnail(file, newFile.genThumbnailPath(), contentType);
      }
    }
  }

  /**
   * Update a file to Cloud SQL and Cloud Storage.
   *
   * @param newFile new file upload to the server
   * @param tags    list of tags label the new file
   * @param file    previously uploaded file
   * @return the updated file
   * @throws IOException if the file cannot be read
   */
  public BaseFile updateFile(MultipartFile newFile, List<String> tags, BaseFile file)
      throws IOException {
    logger.info("entering updateFile()");
    String fileId = file.getId();
    Date createTime = file.getCreateTime();

    if (newFile == null) {
      String pathId = LdsUtil.getPathId(file.getPath());
      return createOrUpdateFileMeta(
          tags, fileId, pathId, file.getName(), file.getSize(), createTime);
    }

    storageService.delete(getBucketName(), file.getPath());
    storageService.delete(getBucketName(), file.genThumbnailPath());
    String newFileId = LdsUtil.generateUuid();
    return createOrUpdateFile(newFile, tags, fileId, newFileId, createTime);
  }

  /**
   * Delete a file from Cloud SQL and Cloud Storage.
   *
   * @param file the uploaded file
   */
  public void deleteFile(BaseFile file) {
    logger.info("entering deleteFile()");
    fileMetaRepo.deleteById(file.getId());
    storageService.delete(getBucketName(), file.getPath());
    storageService.delete(getBucketName(), file.genThumbnailPath());
  }

  /**
   * Search files with given tags.
   *
   * @param tags    list of tags label the files
   * @param orderNo application defined column for referencing order
   * @param size    number of files return
   * @return list of uploaded files
   */
  public List<BaseFile> getFilesByTag(List<String> tags, String orderNo, int size) {
    logger.info("entering getFilesByTag()");
    List<FileMeta> fileMetas = fileMetaRepo.listByTag(tags, orderNo, size);
    return LdsUtil.convertToBaseFile(fileMetas, getBasePath());
  }

  /**
   * Search a single file with given fileId.
   *
   * @param fileId unique id of the file
   * @return the uploaded file
   */
  public BaseFile getFileById(String fileId) {
    logger.info("entering getFileById()");
    return LdsUtil.convertToBaseFile(fileMetaRepo.findById(fileId).orElse(null), getBasePath());
  }

  /**
   * Delete all files from Cloud SQL and Cloud Storage.
   */
  public void resetFile() {
    logger.info("entering resetFile()");
    fileMetaRepo.deleteAll();
    storageService.batchDelete(getBucketName());
  }

  /**
   * Count the number of files in database.
   *
   * @return number of files
   */
  public long count() {
    return fileMetaRepo.count();
  }

  /**
   * Set the application initialized flag.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean setAppInitializedWithLock() {
    return appConfigRepo.setAppInitializedWithLock(getAppName());
  }

  /**
   * Create or update a file in Cloud Storage with the given fileId.
   *
   * @param file       file upload to the server
   * @param tags       list of tags label the file
   * @param fileId     unique ID of the file
   * @param newFileId  unique ID of the new file (for referencing Cloud Storage)
   * @param createTime create time of the file
   * @return file data
   * @throws IOException if the file cannot be read
   */
  private BaseFile createOrUpdateFile(
      MultipartFile file, List<String> tags, String fileId, String newFileId, Date createTime)
      throws IOException {
    String name = file.getOriginalFilename();
    long size = file.getSize();

    BaseFile newFile = createOrUpdateFileMeta(tags, fileId, newFileId, name, size, createTime);
    saveToGcs(newFile, file.getContentType(), file.getBytes());

    if (newFile.checkImageFileType()) {
      createThumbnail(file, newFile.genThumbnailPath());
    }
    return newFile;
  }

  /**
   * Create or update the metadata of a file in Cloud SQL with the given fileId.
   *
   * @param tags       list of tags label the file
   * @param fileId     unique id of the file
   * @param newFileId  unique id of the new file (for referencing Cloud Storage)
   * @param name       name of the file
   * @param size       size of the file
   * @param createTime create time of the file
   * @return file data
   */
  private BaseFile createOrUpdateFileMeta(
      List<String> tags, String fileId, String newFileId, String name, long size, Date createTime) {
    String fileBucketPath = LdsUtil.getFileBucketPath(getBasePath(), newFileId);
    FileMeta fileMeta = new FileMeta(fileId, fileBucketPath, name, tags, size, createTime);
    fileMetaRepo.save(fileMeta);
    return getFileById(fileId);
  }

  /**
   * Create a thumbnail of the given file.
   *
   * @param file        file to create thumbnail
   * @param thumbnailId unique id of the thumbnail file
   */
  private void createThumbnail(MultipartFile file, String thumbnailId) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    Thumbnails.of(file.getInputStream())
        .size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
        .keepAspectRatio(false)
        .toOutputStream(byteArrayOutputStream);

    storageService.save(
        getBucketName(), thumbnailId, file.getContentType(), byteArrayOutputStream.toByteArray());
  }

  private void createThumbnail(File file, String fileId, String contentType) {
    try (InputStream inputStream = new FileInputStream(file);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      Thumbnails.of(inputStream)
          .size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
          .keepAspectRatio(false)
          .toOutputStream(outputStream);

      storageService.save(getBucketName(), fileId, contentType, outputStream.toByteArray());
    } catch (IOException e) {
      logger.error("error creating thumbnail for file: {}", file.getName(), e);
      // ignore the exception and continue
    }
  }

  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getAppName() {
    return appName;
  }

  /**
   * Save the file to Cloud Storage.
   *
   * @param newFile     file data
   * @param contentType content type of the file
   * @param content     content of the file
   */
  private void saveToGcs(BaseFile newFile, String contentType, byte[] content) {
    storageService.save(getBucketName(), newFile.getPath(), contentType, content);
  }
}
