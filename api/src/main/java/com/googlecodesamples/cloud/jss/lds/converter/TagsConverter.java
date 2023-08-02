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

package com.googlecodesamples.cloud.jss.lds.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * Convert tags to and from JSON.
 */
public class TagsConverter implements AttributeConverter<List<String>, String> {

  private static final Gson GSON = new Gson();

  @Override
  public String convertToDatabaseColumn(List<String> tags) {
    return GSON.toJson(tags);
  }

  @Override
  public List<String> convertToEntityAttribute(String tags) {
    return GSON.fromJson(tags, new TypeToken<ArrayList<String>>() {}.getType());
  }
}
