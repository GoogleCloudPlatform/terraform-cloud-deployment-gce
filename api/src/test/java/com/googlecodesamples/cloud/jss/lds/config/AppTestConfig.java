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
package com.googlecodesamples.cloud.jss.lds.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Test configuration properties.
 */
@ConfigurationProperties(prefix = "test")
@TestConfiguration
public class AppTestConfig {

  private static final String MSG_WARN_VAR_NOT_SET =
      "required environment variable '%s' has not been set, test skipped.";

  private static final String ENV_DS_INSTANCE_NAME = "LDS_MYSQL_INSTANCE";

  private static final String ENV_DS_DATABASE_NAME = "LDS_MYSQL_DATABASE";

  private static final String ENV_SPRING_DS_USERNAME = "LDS_MYSQL_USERNAME";

  private static final String ENV_SPRING_DS_PASSWORD = "LDS_MYSQL_PASSWORD";

  private static final String PROP_DS_SQL_INIT_MODE = "never";

  @Autowired
  private Environment env;

  private String appName;

  private String basePath;

  private String testId;

  private String testTags;

  private String testContent;

  private Integer listSize;

  private String orderNum;

  private String healthCheckUrl;

  private String fileUrl;

  private String unknownUrl;

  private String mimetypePlainText;

  private String mimetypeXGzip;

  private String fileExtText;

  private String fileExtTar;

  private String sampleTextFileName;

  private String sampleTarFileName;

  private String sampleImageFileName;

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getTestTags() {
    return testTags;
  }

  public void setTestTags(String testTags) {
    this.testTags = testTags;
  }

  public String getHealthCheckUrl() {
    return healthCheckUrl;
  }

  public void setHealthCheckUrl(String healthCheckUrl) {
    this.healthCheckUrl = healthCheckUrl;
  }

  public String getFileUrl() {
    return fileUrl;
  }

  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public String getUnknownUrl() {
    return unknownUrl;
  }

  public void setUnknownUrl(String unknownUrl) {
    this.unknownUrl = unknownUrl;
  }

  public String getTestId() {
    return testId;
  }

  public void setTestId(String testId) {
    this.testId = testId;
  }

  public String getTestContent() {
    return testContent;
  }

  public void setTestContent(String testContent) {
    this.testContent = testContent;
  }

  public String getMimetypePlainText() {
    return mimetypePlainText;
  }

  public void setMimetypePlainText(String mimetypePlainText) {
    this.mimetypePlainText = mimetypePlainText;
  }

  public String getMimetypeXGzip() {
    return mimetypeXGzip;
  }

  public void setMimetypeXGzip(String mimetypeXGzip) {
    this.mimetypeXGzip = mimetypeXGzip;
  }

  public String getFileExtText() {
    return fileExtText;
  }

  public void setFileExtText(String fileExtText) {
    this.fileExtText = fileExtText;
  }

  public String getFileExtTar() {
    return fileExtTar;
  }

  public void setFileExtTar(String fileExtTar) {
    this.fileExtTar = fileExtTar;
  }

  public String getSampleTextFileName() {
    return sampleTextFileName;
  }

  public void setSampleTextFileName(String sampleTextFileName) {
    this.sampleTextFileName = sampleTextFileName;
  }

  public String getSampleTarFileName() {
    return sampleTarFileName;
  }

  public void setSampleTarFileName(String sampleTarFileName) {
    this.sampleTarFileName = sampleTarFileName;
  }

  public String getSampleImageFileName() {
    return sampleImageFileName;
  }

  public void setSampleImageFileName(String sampleImageFileName) {
    this.sampleImageFileName = sampleImageFileName;
  }

  public Integer getListSize() {
    return listSize;
  }

  public void setListSize(Integer listSize) {
    this.listSize = listSize;
  }

  public String getOrderNum() {
    return orderNum;
  }

  public void setOrderNum(String orderNum) {
    this.orderNum = orderNum;
  }

  public String getDsInstanceName() {
    return env.getProperty(ENV_DS_INSTANCE_NAME);
  }

  public String getDsDatabaseName() {
    return env.getProperty(ENV_DS_DATABASE_NAME);
  }

  public String getSpringDsUsername() {
    return env.getProperty(ENV_SPRING_DS_USERNAME);
  }

  public String getSpringDsPassword() {
    return env.getProperty(ENV_SPRING_DS_PASSWORD);
  }

  public String getDsSqlInitMode() {
    return PROP_DS_SQL_INIT_MODE;
  }

  public static List<String> listRequiredProperties() {
    return List.of(ENV_DS_INSTANCE_NAME, ENV_DS_DATABASE_NAME,
        ENV_SPRING_DS_USERNAME, ENV_SPRING_DS_PASSWORD);
  }

  public static boolean hasAllRequiredProperties() {
    return listRequiredProperties().stream().allMatch(p -> StringUtils.hasText(System.getenv(p)));
  }

  public static String findMissingRequiredProperty() {
    String missing = listRequiredProperties().stream().filter(
        p -> !StringUtils.hasText(System.getenv(p))).findFirst().orElse("");

    if (missing.isEmpty()) {
      return missing;
    } else {
      return String.format(MSG_WARN_VAR_NOT_SET, missing);
    }
  }

  @Override
  public String toString() {
    return "AppTestConfig{" +
        "appName='" + appName + '\'' +
        ", basePath='" + basePath + '\'' +
        ", testId='" + testId + '\'' +
        ", testTags='" + testTags + '\'' +
        ", testContent='" + testContent + '\'' +
        ", listSize=" + listSize +
        ", orderNum='" + orderNum + '\'' +
        ", healthCheckUrl='" + healthCheckUrl + '\'' +
        ", fileUrl='" + fileUrl + '\'' +
        ", unknownUrl='" + unknownUrl + '\'' +
        ", mimetypePlainText='" + mimetypePlainText + '\'' +
        ", mimetypeXGzip='" + mimetypeXGzip + '\'' +
        ", fileExtText='" + fileExtText + '\'' +
        ", fileExtTar='" + fileExtTar + '\'' +
        ", sampleTextFileName='" + sampleTextFileName + '\'' +
        ", sampleTarFileName='" + sampleTarFileName + '\'' +
        ", sampleImageFileName='" + sampleImageFileName + '\'' +
        '}';
  }
}
