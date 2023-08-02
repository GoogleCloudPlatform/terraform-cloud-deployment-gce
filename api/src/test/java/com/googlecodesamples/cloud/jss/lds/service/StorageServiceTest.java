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

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.googlecodesamples.cloud.jss.lds.config.AppTestConfig;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link StorageService}.
 */
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(value = AppTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class StorageServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(StorageServiceTest.class);

  @Autowired
  private AppTestConfig config;

  @InjectMocks
  private StorageService storageService;

  @Mock
  private Storage storage;

  @Mock
  private Blob mockedBlob;

  @Before
  public void setup() throws IOException {
    String bucketName = config.getTestId();
    String blobName = config.getSampleTarFileName();
    String contentType = config.getMimetypeXGzip();

    // load the sample tar file
    Resource resource = new ClassPathResource(blobName);
    Path resourcePath = resource.getFile().toPath();
    byte[] content;
    try (InputStream is = resource.getInputStream()) {
      content = StreamUtils.copyToByteArray(is);
    }

    // set up the mocked blob
    when(mockedBlob.getContentType()).thenReturn(contentType);
    when(mockedBlob.getBucket()).thenReturn(bucketName);
    when(mockedBlob.getName()).thenReturn(blobName);
    when(mockedBlob.getSize()).thenReturn((long) content.length);
    when(mockedBlob.getContent()).thenReturn(content);
    doNothing().when(mockedBlob).downloadTo(any());
    logger.info("mocked blob size: {}", mockedBlob.getSize());

    when(storage.get(anyString(), anyString())).thenReturn(mockedBlob);

    // set up temporary download dir for the test
    if (storageService.getTempDir() == null) {
      storageService.setTempDir(Files.createTempDirectory(null).toFile());
      logger.info("temp dir: {}", storageService.getTempDir());

      Path tempFilePath = storageService.getTempDir().toPath().resolve(blobName);
      Files.copy(resourcePath, tempFilePath);
      logger.info("temp file size: {}", tempFilePath.toFile().length());
    }
  }

  @Test
  public void testDownload() throws IOException {
    String bucketName = config.getTestId();
    String blobName = config.getSampleTarFileName();
    File file = storageService.download(bucketName, blobName);

    verify(storage, times(1)).get(bucketName, blobName);
    logger.info("downloaded file: {}", file);

    assertThat(file).isNotNull();
    assertThat(file.exists()).isTrue();
    assertThat(file.isFile()).isTrue();
    assertThat(file.length()).isGreaterThan(0);
    assertThat(file.getName()).isEqualTo(blobName);
  }

  @Test
  public void testExtract() throws IOException, ArchiveException {
    String bucketName = config.getTestId();
    String blobName = config.getSampleTarFileName();

    File file = storageService.download(bucketName, blobName);
    verify(storage, times(1)).get(bucketName, blobName);
    List<File> extracted = storageService.extract(file, null);
    logger.info("extracted files size: {}", extracted.size());

    assertThat(extracted).isNotNull();
    assertThat(extracted).isNotEmpty();
    assertThat(extracted.size()).isEqualTo(4);

    Map<Boolean, List<File>> isDirMap =
        extracted.stream().collect(Collectors.groupingBy(File::isDirectory));

    assertThat(isDirMap.get(true).size()).isEqualTo(2);
    assertThat(isDirMap.get(false).size()).isEqualTo(2);
  }
}
