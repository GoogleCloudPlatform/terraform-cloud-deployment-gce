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
package com.googlecodesamples.cloud.jss.lds.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.googlecodesamples.cloud.jss.lds.config.AppTestConfig;
import com.googlecodesamples.cloud.jss.lds.model.BaseFile;
import com.googlecodesamples.cloud.jss.lds.model.FileMeta;
import com.googlecodesamples.cloud.jss.lds.service.FileService;
import com.googlecodesamples.cloud.jss.lds.util.LdsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link FileController}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(FileController.class)
@EnableConfigurationProperties(AppTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class FileControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(FileControllerTest.class);

  @Autowired
  private AppTestConfig config;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FileService fileService;

  @Test
  public void testHealthCheckReturnsNoContent() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(config.getHealthCheckUrl()))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  public void testGetFilesResponse() throws Exception {
    List<String> tags = List.of(config.getTestTags());
    String orderNo = "";
    int size = config.getListSize();

    // set up mock service response
    List<BaseFile> expectedResp = LdsUtil.genMockBaseFile(size, true, config.getBasePath());
    logger.info("expectedResp: " + expectedResp);
    when(fileService.getFilesByTag(tags, orderNo, size)).thenReturn(expectedResp);

    // set up simulated HTTP request to the service
    MockHttpServletRequestBuilder mockHttpReq =
        MockMvcRequestBuilders.get(config.getFileUrl())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .queryParam(FileMeta.ATTR_TAGS, config.getTestTags())
            .queryParam(FileMeta.ATTR_ORDER_NO, orderNo)
            .queryParam(FileMeta.ATTR_SIZE, String.valueOf(size));

    // generate mock response from the simulated request
    String mockResp =
        mockMvc
            .perform(mockHttpReq)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    logger.info("mockResp: " + mockResp);

    verify(fileService, times(1)).getFilesByTag(tags, orderNo, size);

    // convert the mock response to JSON object
    JsonObject convertedObj = new Gson().fromJson(mockResp, JsonObject.class);
    logger.info("convertedObj: " + convertedObj);

    assertThat(convertedObj.isJsonObject()).isTrue();
    assertThat(mockResp).isEqualTo(convertedObj.toString());
  }

  @Test
  public void testDeleteFileReturnsNotFound() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete(config.getUnknownUrl()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(""));
  }
}
