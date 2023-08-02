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

import com.googlecodesamples.cloud.jss.lds.model.FileMeta;

import java.util.List;

/**
 * This interface provides customized methods for the {@link FileMetaRepository}.
 */
public interface CustomFileMetaRepository {

  List<FileMeta> listByTag(List<String> tags, String orderNo, int limit);
}
