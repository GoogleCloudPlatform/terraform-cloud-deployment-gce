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
package com.googlecodesamples.cloud.jss.lds.repository;

import com.googlecodesamples.cloud.jss.lds.config.AppTestConfig;
import com.googlecodesamples.cloud.jss.lds.model.FileMeta;
import com.googlecodesamples.cloud.jss.lds.util.LdsUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Unit tests for {@link FileMetaRepository}.
 */
@DataJpaTest
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(AppTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class FileMetaRepositoryTest {

  private static final boolean IS_IMAGE = true;

  @Autowired
  private AppTestConfig testConfig;

  @Autowired
  private FileMetaRepository fileMetaRepo;


  @Before
  public void setup() {
    List<FileMeta> files = LdsUtil.genMockFileMeta(testConfig.getListSize(), IS_IMAGE);
    fileMetaRepo.saveAll(files);
  }

  @Test
  public void testListByTag() {
    int size = testConfig.getListSize();
    List<FileMeta> results = fileMetaRepo.listByTag(List.of(testConfig.getTestTags()), null, size);
    assertThat(results.size()).isEqualTo(size);

    String currentTime = String.valueOf(System.currentTimeMillis());
    results = fileMetaRepo.listByTag(new ArrayList<>(), currentTime, size);
    assertThat(results.size()).isEqualTo(size);

    size = 1;
    results = fileMetaRepo.listByTag(new ArrayList<>(), null, size);
    assertThat(results.size()).isEqualTo(size);
  }
}
