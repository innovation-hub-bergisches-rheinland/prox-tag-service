package de.innovationhub.prox.tagservice.utils;


import de.innovationhub.prox.tagservice.net.ProjectClient;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A security component which provides some useful security features and checks to secure the
 * service
 */
@Component
public class WebSecurity {

  private final AuthenticationUtils authenticationUtils;

  @Autowired
  public WebSecurity(AuthenticationUtils authenticationUtils) {
    this.authenticationUtils = authenticationUtils;
  }

  /**
   * Checks whether the requesting User is the creator of the project which is referenced by the
   * TagCollection
   *
   * @param request Request (User UUID will be extracted)
   * @param tagCollectionId The TagCollection ID which should be the same as project ID
   * @param projectClient ProjectClient Bean to retrieve the Creator of project
   * @return true if requesting user is creator of project referenced by TagCollection, otherwise
   *     false
   */
  public boolean checkProjectCreator(
      HttpServletRequest request, UUID tagCollectionId, ProjectClient projectClient) {
    Optional<UUID> optionalUUID = authenticationUtils.getUserUUIDFromRequest(request);
    Optional<UUID> creatorUUID = projectClient.getCreatorIdOfProject(tagCollectionId);
    return creatorUUID.isPresent()
        && optionalUUID.isPresent()
        && creatorUUID.get().equals(optionalUUID.get());
  }
}
