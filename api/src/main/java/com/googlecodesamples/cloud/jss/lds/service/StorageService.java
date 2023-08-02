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

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Backend service controller for CloudStorage
 */
@Service
public class StorageService {

  private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

  private final Storage storage;

  private File tempDir = null;

  public StorageService() {
    this.storage = StorageOptions.getDefaultInstance().getService();
  }

  public StorageService(Storage storage) {
    this.storage = storage;
  }

  /**
   * Save a file to Cloud Storage.
   *
   * @param bucketName  name of the bucket
   * @param fileId      unique id of the file
   * @param contentType content type of the file
   * @param content     content of the file
   */
  public void save(String bucketName, String fileId, String contentType, byte[] content) {
    BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileId).setContentType(contentType).build();
    storage.create(blobInfo, content);
  }

  /**
   * Download a file from Google Cloud Storage.
   *
   * @param bucketName name of the bucket
   * @param blobName   name of the object
   * @return {@link File} the downloaded file
   */
  public File download(String bucketName, String blobName) throws IOException {
    Blob blob = storage.get(bucketName, blobName);
    if (blob == null) {
      logger.info("blob {} does not exist", blobName);
      return null;
    }

    if (getTempDir() == null) {
      this.tempDir = Files.createTempDirectory(null).toFile();
      logger.info("tempDir: {}", tempDir);
    }

    Path localPath = getTempDir().toPath().resolve(blobName);
    blob.downloadTo(localPath);
    long fileSize = Files.size(localPath);
    logger.info("downloaded {} to {}, fileSize: {} bytes", blobName, localPath, fileSize);
    return localPath.toFile();
  }

  /**
   * Extract a tar archive file and convert it to a list of {@link File}.
   *
   * @param tarFile   the tar file to be extracted
   * @param outputDir the output directory
   * @return {@link List} of {@link File} extracted from the tar file
   * @throws IOException      if failed to create a directory
   * @throws ArchiveException if failed to extract the tar file`
   */
  public List<File> extract(File tarFile, File outputDir) throws IOException, ArchiveException {
    if (outputDir == null) {
      outputDir = getTempDir();
    }
    logger.info("extractTarFile: {} to {}", tarFile, outputDir);
    List<File> extractedFiles = new ArrayList<>();

    try (InputStream is = Files.newInputStream(tarFile.toPath());
         BufferedInputStream bufIs = new BufferedInputStream(is);
         GzipCompressorInputStream gzpIs = new GzipCompressorInputStream(bufIs);
         TarArchiveInputStream tarIs = new TarArchiveInputStream(gzpIs)) {

      TarArchiveEntry entry;
      while ((entry = tarIs.getNextTarEntry()) != null) {
        final Path outputPath = getOutputPath(outputDir, entry);
        // create parent directory if not exists
        if (entry.isDirectory()) {
          Files.createDirectories(outputPath);
        } else {
          // create parent directory if not exists
          Path parent = outputPath.getParent();
          if (parent != null) {
            if (Files.notExists(parent)) {
              Files.createDirectories(parent);
            }
          }
          // copy the TarArchiveInputStream to the outputPath
          Files.copy(tarIs, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }
        // add the extracted file to the list
        File outputFile = outputPath.toFile();
        extractedFiles.add(outputFile);
        logger.info("extracted: {}", outputFile.getName());
      }
    }
    logger.info("extractedFiles size: {}", extractedFiles.size());
    return extractedFiles;
  }

  /**
   * Get the valid file path from the {@link TarArchiveEntry}.
   *
   * @param outputDir the output directory
   * @param entry     the {@link TarArchiveEntry} to be converted
   * @return {@link File} converted from the {@link TarArchiveEntry}
   * @throws IOException if failed to create a directory
   */
  private Path getOutputPath(File outputDir, TarArchiveEntry entry) throws IOException {
    Path targetDirResolved = outputDir.toPath().resolve(entry.getName());
    Path normalizePath = targetDirResolved.normalize();
    if (!normalizePath.startsWith(outputDir.toPath())) {
      // https://snyk.io/research/zip-slip-vulnerability
      throw new IOException("Invalid entry: " + entry.getName());
    }
    return normalizePath;
  }

  /**
   * Delete a file with given fileId.
   *
   * @param bucketName name of the bucket
   * @param fileId     unique id of a file
   */
  public void delete(String bucketName, String fileId) {
    storage.delete(bucketName, fileId);
  }

  /**
   * Delete all files in the bucket.
   *
   * @param bucketName name of the bucket
   */
  public void batchDelete(String bucketName) {
    Page<Blob> blobs = storage.list(bucketName);
    if (!blobs.getValues().iterator().hasNext()) {
      return;
    }

    StorageBatch batchRequest = storage.batch();
    for (Blob blob : blobs.iterateAll()) {
      batchRequest.delete(blob.getBlobId());
    }
    batchRequest.submit();
  }

  /**
   * Close the channels and release resources.
   */
  @PreDestroy
  public void close() throws Exception {
    storage.close();
  }

  public File getTempDir() {
    return tempDir;
  }

  public void setTempDir(File tempDir) {
    this.tempDir = tempDir;
  }
}
