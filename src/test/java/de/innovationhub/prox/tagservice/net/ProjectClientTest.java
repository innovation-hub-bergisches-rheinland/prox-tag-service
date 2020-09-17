package de.innovationhub.prox.tagservice.net;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;

@SpringBootTest
@ContextConfiguration(classes = {ProjectClientTest.class})
class ProjectClientTest {
  private static final String PROJECT_SERVICE_ID = "project-service";
  private static final String UUID_REGEX = "[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}";

  @MockBean
  @Qualifier("eurekaClient")
  EurekaClient eurekaClient;

  @Mock InstanceInfo instanceInfo;

  WireMockServer wireMockServer;
  ProjectClient projectClient;

  /**
   * Helper Method which reads a file
   *
   * @param filePath the resource location to resolve: either a "classpath:" pseudo URL, a "file:"
   *     URL, or a plain file path
   * @return the corresponding File read as String
   * @throws IOException if the resource cannot be resolved to a file in the file system
   */
  String readFromFile(String filePath) throws IOException {
    File file = ResourceUtils.getFile(filePath);
    return new String(Files.readAllBytes(file.toPath()));
  }

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.initMocks(ProjectClientTest.class);
    this.wireMockServer =
        new WireMockServer(
            options().dynamicPort().extensions(new ResponseTemplateTransformer(false)));

    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/"))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                    .withBody(readFromFile("classpath:api/project-service/root.json"))
                    .withTransformers("response-template")));

    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/projects"))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                    .withBody(readFromFile("classpath:api/project-service/projects/projects.json"))
                    .withTransformers("response-template")));

    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/projects/search"))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                    .withBody(
                        readFromFile("classpath:api/project-service/projects/search/search.json"))
                    .withTransformers("response-template")));

    this.wireMockServer.start();
    String wireMockUrl = this.wireMockServer.baseUrl();
    when(instanceInfo.getHomePageUrl()).thenReturn(wireMockUrl);
    when(eurekaClient.getNextServerFromEureka(eq(PROJECT_SERVICE_ID), anyBoolean()))
        .thenReturn(instanceInfo);

    projectClient = new ProjectClient(eurekaClient);
  }

  @Test
  void when_get_creator_id_of_project_valid_then_found() throws Exception {
    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/projects/search/findAllByIds"))
            .withQueryParam("projectIds", matching(UUID_REGEX))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                    .withBody(
                        readFromFile(
                            "classpath:api/project-service/projects/search/findAllByIds/singleProjectFound.json"))
                    .withTransformers("response-template")));

    Optional<UUID> creatorUuid = projectClient.getCreatorIdOfProject(UUID.randomUUID());

    assertTrue(creatorUuid.isPresent());
    assertEquals(UUID.fromString("f6b81e40-6bd3-4f51-a65c-b38e805f1125"), creatorUuid.get());
  }

  @Test
  void when_get_creator_id_of_project_invalid_then_not_found() throws Exception {
    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/projects/search/findAllByIds"))
            .withQueryParam("projectIds", matching(UUID_REGEX))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                    .withBody(
                        readFromFile(
                            "classpath:api/project-service/projects/search/findAllByIds/noProjectFound.json"))
                    .withTransformers("response-template")));

    Optional<UUID> creatorUuid = projectClient.getCreatorIdOfProject(UUID.randomUUID());

    assertFalse(creatorUuid.isPresent());
  }
}
