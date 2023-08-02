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
import com.googlecodesamples.cloud.jss.lds.model.AppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Unit tests for {@link AppConfigRepository}.
 */
@DataJpaTest
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(AppTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class AppConfigRepositoryTest {

  @Autowired
  private AppTestConfig testConfig;

  @Autowired
  private AppConfigRepository appConfigRepo;

  @Value("${test.app_name}")
  private String appName;

  @Before
  public void setup() {
    appConfigRepo.save(new AppConfig(appName, false));
  }

  @Test
  public void testFindAllAppConfig() {
    Iterable<AppConfig> configs = appConfigRepo.findAll();
    // convert iterable to list
    List<AppConfig> configList = new ArrayList<>();
    configs.forEach(configList::add);

    assertThat(configList).isNotNull();
    assertThat(configList.size()).isEqualTo(1);

    AppConfig appConfig = configList.get(0);
    assertThat(appConfig.getAppName()).isEqualTo(testConfig.getAppName());
    assertThat(appConfig.isInitialized()).isFalse();
  }

  @Test
  public void testFindAppConfigByKey() {
    AppConfig appConfig = appConfigRepo.findById(testConfig.getAppName()).orElse(null);
    assertThat(appConfig).isNotNull();
    assertThat(appConfig.getAppName()).isEqualTo(testConfig.getAppName());
    assertThat(appConfig.isInitialized()).isFalse();
  }

  @Test
  public void testUpdateAppConfig() {
    AppConfig appConfig = appConfigRepo.findById(testConfig.getAppName()).orElse(null);
    assertThat(appConfig).isNotNull();
    assertThat(appConfig.getAppName()).isEqualTo(testConfig.getAppName());
    assertThat(appConfig.isInitialized()).isFalse();

    appConfig.setInitialized(true);
    appConfigRepo.save(appConfig);

    appConfig = appConfigRepo.findById(testConfig.getAppName()).orElse(null);
    assertThat(appConfig).isNotNull();
    assertThat(appConfig.getAppName()).isEqualTo(testConfig.getAppName());
    assertThat(appConfig.isInitialized()).isTrue();
  }

  @Test
  public void testUpdateAppConfigWithLock() {
    AppConfig appConfig = appConfigRepo.findById(testConfig.getAppName()).orElse(null);
    assertThat(appConfig).isNotNull();
    assertThat(appConfig.getAppName()).isEqualTo(testConfig.getAppName());
    assertThat(appConfig.isInitialized()).isFalse();

    appConfigRepo.setAppInitializedWithLock(appName);

    appConfig = appConfigRepo.findById(testConfig.getAppName()).orElse(null);
    assertThat(appConfig).isNotNull();
    assertThat(appConfig.getAppName()).isEqualTo(testConfig.getAppName());
    assertThat(appConfig.isInitialized()).isTrue();
  }
}
