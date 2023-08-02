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

USE cloud_deployment_gce_db;

CREATE TABLE IF NOT EXISTS fileMetadata (
    id VARCHAR(64),
    path VARCHAR(128),
    name VARCHAR(64),
    orderNo VARCHAR(128),
    size INTEGER,
    tags VARCHAR(256),
    createTime DATETIME(3),
    updateTime DATETIME(3),
    PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS appConfig (
    appName VARCHAR(64),
    initialized TINYINT(1),
    createTime DATETIME(3),
    updateTime DATETIME(3),
    PRIMARY KEY (appName)
) engine=InnoDB;
