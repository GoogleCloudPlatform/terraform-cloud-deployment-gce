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

import com.googlecodesamples.cloud.jss.lds.converter.TagsConverter;
import com.googlecodesamples.cloud.jss.lds.util.LdsUtil;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.List;

/**
 * This class represents the fileMetadata table in Cloud SQL schema
 */
@Entity
@Table(name = FileMeta.TABLE_NAME)
public class FileMeta {

  public static final String TABLE_NAME = "fileMetadata";

  public static final String ATTR_ID = "id";

  public static final String ATTR_PATH = "path";

  public static final String ATTR_NAME = "name";

  public static final String ATTR_TAGS = "tags";

  public static final String ATTR_ORDER_NO = "orderNo";

  public static final String ATTR_SIZE = "size";

  public static final String ATTR_CREATE_TIME = "createTime";

  public static final String ATTR_UPDATE_TIME = "updateTime";

  @Id
  private String id;

  private String path;

  private String name;

  @Convert(converter = TagsConverter.class)
  private List<String> tags;

  private String orderNo;

  private long size;

  private Date createTime;

  private Date updateTime;

  public FileMeta() {
  }

  public FileMeta(
      String id, String path, String name, List<String> tags, long size, Date createTime) {
    this.id = id;
    this.path = path;
    this.name = name;
    this.tags = tags;
    this.orderNo = System.currentTimeMillis() + "-" + LdsUtil.getPathId(path);
    this.size = size;
    this.createTime = createTime;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
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
}
