package io.archilab.prox.tagservice.net;

import com.jayway.jsonpath.PathNotFoundException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.annotation.Lazy;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectClient {
  private final Traverson traverson;
  private static final String PROJECT_SERVICE_ID = "project-service";

  @Autowired
  public ProjectClient(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    URI serviceUri = URI.create(eurekaClient.getNextServerFromEureka(PROJECT_SERVICE_ID, false).getHomePageUrl());
    this.traverson = new Traverson(serviceUri, MediaTypes.HAL_JSON);
  }

  public Optional<UUID> getCreatorIdOfProject(UUID projectId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("projectIds", projectId);
    try {
      String creatorID = traverson.follow("projects", "search", "findAllByIds").withTemplateParameters(parameters).toObject("$._embedded.projects[0].creatorID");
      UUID uuid = UUID.fromString(creatorID);
      return Optional.of(uuid);
    } catch (Exception e) {
      log.error("Could not retrieve creatorID of projectId: " + projectId);
    }

    return Optional.empty();
  }
}