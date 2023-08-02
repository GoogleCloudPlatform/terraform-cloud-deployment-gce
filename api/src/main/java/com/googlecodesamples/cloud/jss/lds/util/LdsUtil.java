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

package com.googlecodesamples.cloud.jss.lds.util;

import com.googlecodesamples.cloud.jss.lds.model.BaseFile;
import com.googlecodesamples.cloud.jss.lds.model.FileMeta;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/** Reusable utility functions */
public class LdsUtil {

  private static final char URL_SLASH = '/';

  private static final String VAR_HOSTNAME = "HOSTNAME";

  /**
   * Get base path of a file.
   *
   * @param basePath the URL without the file ID
   * @return the base path of the input URL
   */
  public static String getResourceBasePath(String basePath) {
    String bucketBasePath = StringUtils.trimLeadingCharacter(basePath, URL_SLASH);
    return basePath.substring(0, basePath.length() - bucketBasePath.length());
  }

  /**
   * Get relative path of a file from the base.
   *
   * @param basePath the URL without the file ID
   * @param fileId the ID of the file
   * @return the full URL of the file
   */
  public static String getFileBucketPath(String basePath, String fileId) {
    String bucketBasePath = StringUtils.trimLeadingCharacter(basePath, URL_SLASH);
    return StringUtils.trimTrailingCharacter(bucketBasePath, URL_SLASH) + URL_SLASH + fileId;
  }

  /**
   * Generate UUID.
   *
   * @return the generated UUID
   */
  public static String generateUuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * Get file ID from the path.
   *
   * @return the ID of the file
   */
  public static String getPathId(String path) {
    String[] pathArr = path.split(String.valueOf(URL_SLASH));
    return pathArr[pathArr.length - 1];
  }

  /**
   * Convert fileMeta retrieved from Database to BaseFile object.
   *
   * @param fileMetas lis of fileMeta retrieved from Database
   * @param basePath the URL without the file ID
   * @return list of files data
   */
  public static List<BaseFile> convertToBaseFile(List<FileMeta> fileMetas, String basePath) {
    if (CollectionUtils.isEmpty(fileMetas)) {
      return null;
    }

    String resourceBasePath = getResourceBasePath(basePath);
    return fileMetas.stream()
        .map(fileMeta -> new BaseFile(fileMeta, resourceBasePath))
        .collect(Collectors.toList());
  }

  public static BaseFile convertToBaseFile(FileMeta fileMeta, String basePath) {
    if (fileMeta == null) {
      return null;
    }

    List<BaseFile> fileList = convertToBaseFile(List.of(fileMeta), basePath);
    if (!CollectionUtils.isEmpty(fileList)) {
      return fileList.get(0);
    }
    return null;
  }

  public static List<FileMeta> genMockFileMeta(int serialNumber, boolean isImage) {
    List<FileMeta> testFileMetas = new ArrayList<>();
    for (int i = 0; i < serialNumber; i++) {
      FileMeta fileMeta = new FileMeta();
      fileMeta.setId("test-" + i);
      fileMeta.setPath("test-path-" + i);
      if (isImage) {
        fileMeta.setName("test-filename-" + i + ".png");
      } else {
        fileMeta.setName("test-filename-" + i);
      }
      fileMeta.setTags(List.of("test-tag"));
      fileMeta.setSize(1000);
      fileMeta.setCreateTime(new Date());
      fileMeta.setUpdateTime(fileMeta.getCreateTime());
      fileMeta.setOrderNo(System.currentTimeMillis() + "-test");
      testFileMetas.add(fileMeta);
    }
    return testFileMetas;
  }

  public static List<BaseFile> genMockBaseFile(int serialNumber, boolean isImage, String basePath) {
    List<FileMeta> fileMetas = genMockFileMeta(serialNumber, isImage);
    return LdsUtil.convertToBaseFile(fileMetas, basePath);
  }

  public static List<BaseFile> genMockBaseFile(List<FileMeta> fileMetas, String basePath) {
    return LdsUtil.convertToBaseFile(fileMetas, basePath);
  }

  public static String getCurrentHostName() {
    return System.getenv(VAR_HOSTNAME);
  }
}
