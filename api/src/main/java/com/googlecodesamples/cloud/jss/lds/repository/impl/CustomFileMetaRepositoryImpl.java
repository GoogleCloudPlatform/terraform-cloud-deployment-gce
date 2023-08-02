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

import com.googlecodesamples.cloud.jss.lds.model.FileMeta;
import com.googlecodesamples.cloud.jss.lds.repository.CustomFileMetaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * This class provides implementation for the FileMetaRepository.
 */
@Component
public class CustomFileMetaRepositoryImpl implements CustomFileMetaRepository {

  private static final Logger logger = LoggerFactory.getLogger(CustomFileMetaRepositoryImpl.class);

  private static final String SQL_SEARCH_BY_ORDER_NO =
      "select * from " + FileMeta.TABLE_NAME + " where " + FileMeta.ATTR_ORDER_NO + " < ? ";

  private static final String SQL_ORDER_BY_ORDER_NO =
      "order by " + FileMeta.ATTR_ORDER_NO + " desc limit ?";

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<FileMeta> listByTag(List<String> tags, String orderNo, int limit) {
    // create sql
    StringBuilder sql = new StringBuilder(SQL_SEARCH_BY_ORDER_NO);
    if (!CollectionUtils.isEmpty(tags)) {
      sql.append("and ( ");
      for (int i = 0; i < tags.size(); i++) {
        sql.append("tags like ?");
        if (i != tags.size() - 1) {
          sql.append(" or ");
        }
      }
      sql.append(" ) ");
    }
    sql.append(SQL_ORDER_BY_ORDER_NO);

    logger.info("sql: {}", sql);

    // set parameter
    if (!StringUtils.hasText(orderNo)) {
      orderNo = String.valueOf(Long.MAX_VALUE);
    }

    int index = 1;
    Query query = entityManager.createNativeQuery(sql.toString(), FileMeta.class);
    query.setParameter(index, orderNo);
    for (String tag : tags) {
      query.setParameter(++index, "%\"" + tag + "\"%");
    }
    query.setParameter(++index, limit);

    return (List<FileMeta>) query.getResultList();
  }
}
