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

package de.innovationhub.prox.tagservice.net;

import com.netflix.discovery.EurekaClient;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    URI serviceUri =
        URI.create(
            eurekaClient.getNextServerFromEureka(PROJECT_SERVICE_ID, false).getHomePageUrl());
    this.traverson = new Traverson(serviceUri, MediaTypes.HAL_JSON);
  }

  public Optional<UUID> getCreatorIdOfProject(UUID projectId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("projectIds", projectId);
    try {
      String creatorID =
          traverson
              .follow("projects", "search", "findAllByIds")
              .withTemplateParameters(parameters)
              .toObject("$._embedded.projects[0].creatorID");
      UUID uuid = UUID.fromString(creatorID);
      return Optional.of(uuid);
    } catch (Exception e) {
      log.error("Could not retrieve creatorID of projectId: " + projectId);
    }

    return Optional.empty();
  }
}
