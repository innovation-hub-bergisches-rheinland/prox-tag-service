package de.innovationhub.prox.tagservice.net;


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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectClient {
  private static final Logger logger = LoggerFactory.getLogger(ProjectClient.class);
  private final URI projectServiceUri;

  @Autowired
  public ProjectClient(@Value("${prox.services.project-service.url}") URI projectServiceUri) {
    this.projectServiceUri = projectServiceUri;
  }

  public Optional<UUID> getCreatorIdOfProject(UUID projectId) {
    try {
      var traverson = this.getTraversonInstance();
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

  private Traverson getTraversonInstance() {
    return new Traverson(projectServiceUri, MediaTypes.HAL_JSON);
  }
}
