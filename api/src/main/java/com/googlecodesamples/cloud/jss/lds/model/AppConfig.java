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
package com.googlecodesamples.cloud.jss.lds.model;

import jakarta.persistence.*;

import java.util.Date;

/**
 * This class represents the appConfig table in Cloud SQL schema
 */
@Entity
@Table(name = AppConfig.TABLE_NAME)
public class AppConfig {

  public static final String ENTITY_NAME = "AppConfig";

  public static final String TABLE_NAME = "appConfig";

  public static final String ATTR_APP_NAME = "appName";

  public static final String ATTR_INITIALIZED = "initialized";

  public static final String ATTR_CREATE_TIME = "createTime";

  public static final String ATTR_UPDATE_TIME = "updateTime";

  @Id
  private String appName;

  private boolean initialized;

  private Date createTime;

  private Date updateTime;

  public AppConfig() {
  }

  public AppConfig(String appName, boolean initialized) {
    this.appName = appName;
    this.initialized = initialized;
  }

  @PrePersist
  public void prePersist() {
    Date currentDate = new Date();
    setCreateTime(currentDate);
    setUpdateTime(currentDate);
  }

  @PreUpdate
  public void preUpdate() {
    setUpdateTime(new Date());
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public String toString() {
    return "AppConfig{" +
        "appName='" + appName + '\'' +
        ", initialized=" + initialized +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        '}';
  }
}
