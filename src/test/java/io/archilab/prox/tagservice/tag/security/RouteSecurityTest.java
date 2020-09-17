/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.archilab.prox.tagservice.tag.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.archilab.prox.tagservice.net.ProjectClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RouteSecurityTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ProjectClient projectClient;

  @BeforeEach
  void init() {
    MockitoAnnotations.initMocks(RouteSecurityTest.class);
  }

  void performRequest(
      HttpMethod httpMethod, String url, Object content, ResultMatcher expectedResult)
      throws Exception {
    mockMvc.perform(request(httpMethod, url)
        .accept(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(content)))
        .andDo(print())
        .andExpect(expectedResult)
        .andReturn();
  }

  void performHalRequest(HttpMethod httpMethod, String url, String content, ResultMatcher expectedResult) throws Exception {
    mockMvc.perform(request(httpMethod, url)
        .accept(MediaType.APPLICATION_JSON)
        .contentType("text/uri-list"))
        .andDo(print())
        .andExpect(expectedResult)
        .andReturn();
  }

  String buildUrl(String url, Object... vars) {
    return UriComponentsBuilder.fromUriString(url).buildAndExpand(vars).encode().toString();
  }

}
