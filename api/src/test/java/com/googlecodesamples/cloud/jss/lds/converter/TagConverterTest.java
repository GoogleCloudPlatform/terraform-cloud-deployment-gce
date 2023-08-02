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

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;

/**
 * Unit tests for {@link TagsConverter}.
 */
public class TagConverterTest {

  private static final List<String> EXPECTED_TAGS_LIST = List.of("test-tag");

  private static final String EXPECTED_TAGS_STRING = "[\"test-tag\"]";

  private final TagsConverter tagsConverter = new TagsConverter();

  @Test
  public void testConvertToDatabaseColumn() {
    String tags = tagsConverter.convertToDatabaseColumn(EXPECTED_TAGS_LIST);
    assertThat(tags).isEqualTo(EXPECTED_TAGS_STRING);
  }

  @Test
  public void testConvertToEntityAttribute() {
    List<String> tags = tagsConverter.convertToEntityAttribute(EXPECTED_TAGS_STRING);
    assertThat(tags).isEqualTo(EXPECTED_TAGS_LIST);
  }
}
