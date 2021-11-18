package de.innovationhub.prox.tagservice.net;


import com.netflix.discovery.EurekaClient;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectClient {
  private static final String PROJECT_SERVICE_ID = "project-service";
  private static final Logger logger = LoggerFactory.getLogger(ProjectClient.class);
  private final EurekaClient eurekaClient;

  @Autowired
  public ProjectClient(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  public Optional<UUID> getCreatorIdOfProject(UUID projectId) {
    try {
      Traverson traverson;

      /*
       * TODO
       * In a perfect world the Traverson instance would only be created from scratch when it is
       * necessary. Hence the client would need to keep track about the instances health.
       * Currently this is necessary in order to ensure that tags can be created when a project is
       * created or edited. Alternatively an other bean scope would do the job.
       */
      var traversonInstance = this.getTraversonInstance();
      if (traversonInstance.isPresent()) {
        traverson = traversonInstance.get();
      } else {
        log.error("Could not create Traverson instance");
        traverson = getTraversonInstance().orElseThrow(Exception::new);
      }
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("projectIds", projectId);
      String creatorID =
          traverson
              .follow("projects", "search", "findAllByIds")
              .withTemplateParameters(parameters)
              .toObject("$._embedded.projects[0].creatorID");
      UUID uuid = UUID.fromString(creatorID);
      return Optional.of(uuid);
    } catch (Exception e) {
      log.error("Could not retrieve creatorID of projectId: " + projectId, e);
    }

    return Optional.empty();
  }

  private Optional<Traverson> getTraversonInstance() {
    return this.getProjectServiceUri().map(uri -> new Traverson(uri, MediaTypes.HAL_JSON));
  }

  private Optional<URI> getProjectServiceUri() {
    try {
      URI serviceUri =
          URI.create(
              eurekaClient.getNextServerFromEureka(PROJECT_SERVICE_ID, false).getHomePageUrl());
      return Optional.of(serviceUri);
    } catch (Exception e) {
      logger.error("Could not retrieve " + PROJECT_SERVICE_ID + " URL from Eureka", e);
    }
    return Optional.empty();
  }
}
