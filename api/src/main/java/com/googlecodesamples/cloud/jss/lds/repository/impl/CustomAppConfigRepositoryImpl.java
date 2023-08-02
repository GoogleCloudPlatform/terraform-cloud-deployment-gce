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
package com.googlecodesamples.cloud.jss.lds.repository.impl;

import com.googlecodesamples.cloud.jss.lds.model.AppConfig;
import com.googlecodesamples.cloud.jss.lds.repository.CustomAppConfigRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides implementation for the AppConfigRepository.
 */
@Component
public class CustomAppConfigRepositoryImpl implements CustomAppConfigRepository {

  private static final Logger logger = LoggerFactory.getLogger(CustomAppConfigRepositoryImpl.class);

  private static final String SQL_SELECT_ENTITY_BY_KEY = "SELECT e FROM %s e WHERE e.%s = :%s";

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public boolean setAppInitializedWithLock(String key) {
    boolean isImportTriggered = true;
    String tblName = AppConfig.ENTITY_NAME;
    String appName = AppConfig.ATTR_APP_NAME;
    String sql = String.format(SQL_SELECT_ENTITY_BY_KEY, tblName, appName, appName);
    logger.info("sql: {}", sql);

    // retrieve the appConfig and lock the row with select-for-update to prevent concurrent update
    AppConfig config = entityManager.createQuery(sql, AppConfig.class)
        .setParameter(appName, key)
        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        .getSingleResult();
    logger.info("appConfig from db: {}", config);

    if (config.isInitialized()) {
      logger.info("appConfig is already initialized");
      return isImportTriggered;
    }

    // the updated value will be committed when the transaction is committed
    isImportTriggered = false;
    config.setInitialized(true);
    logger.info("updated appConfig: {}", config);
    return isImportTriggered;
  }
}
