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
package com.googlecodesamples.cloud.jss.lds;

import com.googlecodesamples.cloud.jss.lds.service.FileService;
import com.googlecodesamples.cloud.jss.lds.service.StorageService;
import com.googlecodesamples.cloud.jss.lds.util.LdsUtil;
import org.apache.commons.compress.archivers.ArchiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * This class provides callback functions for application events.
 */
@Component
public class AppEventListener {

  private static final Logger logger = LoggerFactory.getLogger(AppEventListener.class);

  @Autowired
  private FileService fileService;

  @Autowired
  private StorageService storageService;

  @Value("${data.init.bucket.name}")
  private String bucketName;

  @Value("${data.init.file.name}")
  private String fileName;

  /**
   * This function is called when the application is started.
   * It checks if there is any data in the database.
   * If not, it imports the default data from a tar archive.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onStartup() {
    String hostname = LdsUtil.getCurrentHostName();
    logger.info("Application started. Checking for data initialization on node: {}", hostname);
    try {
      // acquire a row lock to prevent other instances from running the initialization
      boolean isImportTriggered = fileService.setAppInitializedWithLock();
      if (isImportTriggered) {
        logger.info("Data initialization finished or in progress. Skipping on node: {}", hostname);
        return;
      }

      logger.info("Data initialization started, bucket: {}, file: {}, node: {}",
          bucketName, fileName, hostname);
      // download the tar archive from a public Google Cloud Storage bucket
      File initTarArchive = storageService.download(bucketName, fileName);
      List<File> extractedFiles = storageService.extract(initTarArchive, null);

      // import the metadata into the database and upload the files to Google Cloud Storage
      fileService.importFiles(extractedFiles, Collections.emptyList());

      // check the integrity of the imported data by counting the number of files
      long count = fileService.count();
      logger.info("Data initialization completed. Imported {} files", count);
    } catch (IOException | ArchiveException e) {
      logger.error("Data initialization failed.", e);
      // ignore the exception and continue
    }
  }
}
