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
