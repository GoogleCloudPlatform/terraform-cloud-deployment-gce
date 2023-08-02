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

import static com.google.common.truth.Truth.assertThat;

import com.googlecodesamples.cloud.jss.lds.util.LdsUtil;
import org.junit.Test;

/**
 * Unit tests for {@link BaseFile}.
 */
public class BaseFileTest {

  private static final String BASE_PATH = "/test";

  @Test
  public void testGetThumbnailPath() {
    BaseFile file = LdsUtil.genMockBaseFile(1, true, BASE_PATH).get(0);
    String expected = "test-path-0_small";
    assertThat(file.genThumbnailPath()).isEqualTo(expected);
  }

  @Test
  public void testIsFile() {
    BaseFile file = LdsUtil.genMockBaseFile(1, false, BASE_PATH).get(0);
    assertThat(file.checkImageFileType()).isFalse();
  }

  @Test
  public void testIsImage() {
    BaseFile file = LdsUtil.genMockBaseFile(1, true, BASE_PATH).get(0);
    assertThat(file.checkImageFileType()).isTrue();
  }
}
