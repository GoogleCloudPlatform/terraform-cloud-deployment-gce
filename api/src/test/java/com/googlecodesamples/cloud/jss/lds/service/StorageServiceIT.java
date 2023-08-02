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

import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.googlecodesamples.cloud.jss.lds.config.AppTestConfig;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Integration tests for {@link StorageService}.
 */
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(value = AppTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class StorageServiceIT {

  private static final Logger logger = LoggerFactory.getLogger(StorageServiceIT.class);

  private static final String BUCKET_NAME = RemoteStorageHelper.generateBucketName();

  private static Storage storage;

  private static StorageService storageService;

  @Autowired
  private AppTestConfig config;

  @BeforeClass
  public static void setUp() {
    storageService = new StorageService();
    RemoteStorageHelper helper = RemoteStorageHelper.create();
    storage = helper.getOptions().getService();
    storage.create(BucketInfo.of(BUCKET_NAME));
  }

  @AfterClass
  public static void tearDown() throws Exception {
    if (storage != null) {
      RemoteStorageHelper.forceDelete(storage, BUCKET_NAME);
    }
    storageService.close();
  }

  @Test
  public void testSaveBlob() {
    String fileId = saveTestFile();
    assertThat(storage.get(BUCKET_NAME, fileId)).isNotNull();
    storage.delete(BUCKET_NAME, fileId);
  }

  @Test
  public void testSaveTarArchive() throws IOException {
    String fileId = saveTarArchive();
    assertThat(storage.get(BUCKET_NAME, fileId)).isNotNull();
    storage.delete(BUCKET_NAME, fileId);
  }

  @Test
  public void testDeleteBlob() {
    String fileId = saveTestFile();
    storageService.delete(BUCKET_NAME, fileId);
    assertThat(storage.get(BUCKET_NAME, fileId)).isNull();
  }

  @Test
  public void testBatchDeleteBlob() {
    String fileId = saveTestFile();
    storageService.batchDelete(BUCKET_NAME);
    assertThat(storage.get(BUCKET_NAME, fileId)).isNull();
  }

  @Test
  public void testDownloadBlob() throws IOException {
    String fileId = saveTestFile();
    File file = storageService.download(BUCKET_NAME, fileId);
    assertThat(file).isNotNull();
    assertThat(file.exists()).isTrue();
    assertThat(file.isFile()).isTrue();
    assertThat(file.getPath().endsWith(config.getFileExtText())).isTrue();
  }

  @Test
  public void testDownloadAndExtractTarArchive() throws IOException, ArchiveException {
    String fileId = saveTarArchive();
    File file = storageService.download(BUCKET_NAME, fileId);
    assertThat(file).isNotNull();
    assertThat(file.exists()).isTrue();
    assertThat(file.isFile()).isTrue();
    assertThat(file.getPath().endsWith(config.getFileExtTar())).isTrue();

    List<File> extracted = storageService.extract(file, file.getParentFile());
    logger.info("extracted files size: {}", extracted.size());
    assertThat(extracted).isNotNull();
    assertThat(extracted).isNotEmpty();
    assertThat(extracted.size()).isEqualTo(4);

    Map<Boolean, List<File>> isDirMap =
        extracted.stream().collect(Collectors.groupingBy(File::isDirectory));

    assertThat(isDirMap.get(true).size()).isEqualTo(2);
    assertThat(isDirMap.get(false).size()).isEqualTo(2);
  }

  private String saveTestFile() {
    String fileId = config.getSampleTextFileName();
    byte[] content = config.getTestContent().getBytes(UTF_8);
    storageService.save(BUCKET_NAME, fileId, config.getMimetypePlainText(), content);
    return fileId;
  }

  private String saveTarArchive() throws IOException {
    String fileName = config.getSampleTarFileName();
    Resource resource = new ClassPathResource(fileName);
    try (InputStream is = resource.getInputStream()) {
      byte[] content = StreamUtils.copyToByteArray(is);
      storageService.save(BUCKET_NAME, fileName, config.getMimetypeXGzip(), content);
    }
    return fileName;
  }
}
