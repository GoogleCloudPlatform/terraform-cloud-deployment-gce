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

import com.googlecodesamples.cloud.jss.lds.config.AppTestConfig;
import com.googlecodesamples.cloud.jss.lds.model.BaseFile;
import com.googlecodesamples.cloud.jss.lds.model.FileMeta;
import com.googlecodesamples.cloud.jss.lds.repository.FileMetaRepository;
import com.googlecodesamples.cloud.jss.lds.util.LdsUtil;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FileService}.
 */
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(AppTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class FileServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(FileServiceTest.class);

  private static final boolean IS_IMAGE = true;

  private List<MultipartFile> mockMultipartFiles;

  private List<BaseFile> mockFiles;

  private List<String> tags;

  @Autowired
  private AppTestConfig config;

  @InjectMocks
  private FileService fileService;

  @Mock
  private StorageService storageService;

  @Mock
  private FileMetaRepository fileMetaRepo;

  @Before
  public void setup() throws Exception {
    Integer listSize = config.getListSize();
    Resource resource = new ClassPathResource(config.getSampleImageFileName());
    mockMultipartFiles = new ArrayList<>();
    mockFiles = new ArrayList<>();
    tags = List.of(config.getTestTags());
    fileService.setBasePath(config.getBasePath());

    for (int i = 0; i < listSize; i++) {
      mockMultipartFiles.add(new MockMultipartFile(config.getTestId(), resource.getInputStream()));
    }
    logger.info("mockMultipartFiles size: {}", mockMultipartFiles.size());

    List<FileMeta> mockList = LdsUtil.genMockFileMeta(listSize, IS_IMAGE);
    mockFiles = LdsUtil.genMockBaseFile(mockList, config.getBasePath());
    logger.info("mockFiles size: {}", mockFiles.size());

    // set up mock service responses
    when(fileMetaRepo.save(any())).thenReturn(mockList.get(0));
    when(fileMetaRepo.findById(any())).thenReturn(Optional.ofNullable(mockList.get(0)));
    when(fileMetaRepo.listByTag(tags, config.getOrderNum(), listSize)).thenReturn(mockList);
    doNothing().when(storageService).save(any(), any(), any(), any());
    doNothing().when(storageService).delete(any(), any());
    doNothing().when(storageService).batchDelete(any());
  }

  @Test
  public void testUploadFiles() throws IOException {
    List<BaseFile> files = fileService.uploadFiles(mockMultipartFiles, tags);
    assertThat(files).isNotEmpty();
    assertThat(files.size()).isEqualTo(config.getListSize());
    assertThat(files.get(0).checkImageFileType()).isTrue();
    assertThat(files.get(0).getTags()).isEqualTo(tags);
  }

  @Test
  public void testUpdateFile() throws IOException {
    BaseFile file = fileService.updateFile(mockMultipartFiles.get(0), tags, mockFiles.get(0));
    assertThat(file).isNotNull();
    assertThat(file.checkImageFileType()).isTrue();
    assertThat(file.getTags()).isEqualTo(tags);
  }

  @Test
  public void testGetFilesByTag() {
    Integer listSize = config.getListSize();
    List<BaseFile> files = fileService.getFilesByTag(tags, config.getOrderNum(), listSize);
    assertThat(files).isNotEmpty();
    assertThat(files.size()).isEqualTo(listSize);
    assertThat(files.get(0).checkImageFileType()).isTrue();
    assertThat(files.get(0).getTags()).isEqualTo(tags);
  }

  @Test
  public void testGetFilesByUnknownTag() {
    List<String> unknownTags = List.of("test", "tag");
    List<BaseFile> files =
        fileService.getFilesByTag(unknownTags, config.getOrderNum(), config.getListSize());
    assertThat(files).isNull();
  }

  @Test
  public void testGetFileById() {
    BaseFile file = fileService.getFileById(config.getTestId());
    assertThat(file).isNotNull();
    assertThat(file.checkImageFileType()).isTrue();
    assertThat(file.getTags()).isEqualTo(tags);
  }
}

