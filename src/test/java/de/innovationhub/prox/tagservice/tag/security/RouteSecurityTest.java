package de.innovationhub.prox.tagservice.tag.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.tagservice.net.ProjectClient;
import de.innovationhub.prox.tagservice.tag.TagEventHandler;
import de.innovationhub.prox.tagservice.tagcollection.TagCollectionEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RouteSecurityTest {

  @Autowired MockMvc mockMvc;

  @MockBean ProjectClient projectClient;

  @MockBean
  TagEventHandler tagEventHandler;

  @MockBean
  TagCollectionEventHandler tagCollectionEventHandler;

  @BeforeEach
  void init() {}

  void performRequest(
      HttpMethod httpMethod, String url, Object content, ResultMatcher expectedResult)
      throws Exception {
    mockMvc
        .perform(
            request(httpMethod, url)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(content)))
        .andDo(print())
        .andExpect(expectedResult)
        .andReturn();
  }

  void performHalRequest(
      HttpMethod httpMethod, String url, String content, ResultMatcher expectedResult)
      throws Exception {
    mockMvc
        .perform(
            request(httpMethod, url)
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
