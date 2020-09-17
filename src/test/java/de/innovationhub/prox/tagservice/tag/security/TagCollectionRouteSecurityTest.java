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

package de.innovationhub.prox.tagservice.tag.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagCollection;
import de.innovationhub.prox.tagservice.tag.TagCollectionRepository;
import de.innovationhub.prox.tagservice.tag.TagName;
import de.innovationhub.prox.tagservice.tag.TagRepository;
import de.innovationhub.prox.tagservice.utils.AuthenticationUtils;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;

public class TagCollectionRouteSecurityTest extends RouteSecurityTest {
  private static final String TAG_COLLECTIONS_ROUTE = "/tagCollections/";
  private static final String TAG_COLLECTIONS_ID_ROUTE = "/tagCollections/{id}";
  private static final String TAG_COLLECTIONS_ID_TAGS_ROUTE = "/tagCollections/{id}/tags";
  private static final String TAG_ID_ROUTE = "/tags/{id}";

  private final UUID USER_ID = UUID.randomUUID();

  @Autowired TagCollectionRepository tagCollectionRepository;

  @Autowired TagRepository tagRepository;

  @MockBean AuthenticationUtils authenticationUtils;

  @BeforeEach
  @Override
  void init() {
    super.init();
    tagCollectionRepository.deleteAll();
    tagRepository.deleteAll();
    MockitoAnnotations.initMocks(TagCollectionRouteSecurityTest.class);
    Mockito.when(authenticationUtils.getUserUUIDFromRequest(Mockito.any(HttpServletRequest.class)))
        .thenReturn(Optional.of(USER_ID));
  }

  private TagCollection createTestTagCollection() {
    return new TagCollection(UUID.randomUUID());
  }

  private Tag createTestTag() {
    return new Tag(new TagName("Test Tag"));
  }

  // GET /tagCollections
  @Test
  void when_unauthenticated_user_performs_get_tag_collections_then_is_ok() throws Exception {
    performRequest(HttpMethod.GET, TAG_COLLECTIONS_ROUTE, null, status().isOk());
  }

  // POST /tagCollections/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_post_tag_collections_then_is_forbidden()
      throws Exception {
    performRequest(
        HttpMethod.POST,
        buildUrl(TAG_COLLECTIONS_ID_ROUTE, UUID.randomUUID()),
        null,
        status().isForbidden());
  }

  // DELETE /tagCollections/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_performs_delete_tag_collections_then_is_forbidden()
          throws Exception {
    performRequest(
        HttpMethod.DELETE,
        buildUrl(TAG_COLLECTIONS_ID_ROUTE, UUID.randomUUID()),
        null,
        status().isForbidden());
  }

  // PUT /tagCollections/{id}/tags
  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_creator_performs_put_tag_collections_tags_then_is_no_content()
          throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());
    Tag tag = tagRepository.save(createTestTag());

    Mockito.when(projectClient.getCreatorIdOfProject(Mockito.any(UUID.class)))
        .thenReturn(Optional.of(USER_ID));

    performHalRequest(
        HttpMethod.PUT,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        buildUrl(TAG_ID_ROUTE, tag.getId()),
        status().isNoContent());
  }

  // PUT /tagCollections/{id}/tags
  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_not_creator_performs_put_tag_collections_tags_then_is_forbidden()
          throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());
    Tag tag = tagRepository.save(createTestTag());

    Mockito.when(projectClient.getCreatorIdOfProject(Mockito.any(UUID.class)))
        .thenReturn(Optional.of(UUID.randomUUID()));

    performHalRequest(
        HttpMethod.PUT,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        buildUrl(TAG_ID_ROUTE, tag.getId()),
        status().isForbidden());
  }

  // PUT /tagCollections/{id}/tags
  @WithMockUser(roles = {})
  @Test
  void
      when_authenticated_user_does_not_have_role_professor_performs_put_tag_collections_tags_then_is_forbidden()
          throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());

    performHalRequest(
        HttpMethod.PUT,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        null,
        status().isForbidden());
  }

  // PUT /tagCollections/{id}/tags
  @Test
  void when_unauthenticated_user_performs_put_tag_collections_tags_then_is_unauthorized()
      throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());

    performHalRequest(
        HttpMethod.PUT,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        null,
        status().isUnauthorized());
  }

  // POST /tagCollections/{id}/tags
  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_creator_performs_post_tag_collections_tags_then_is_no_content()
          throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());
    Tag tag = tagRepository.save(createTestTag());

    Mockito.when(projectClient.getCreatorIdOfProject(Mockito.any(UUID.class)))
        .thenReturn(Optional.of(USER_ID));

    performHalRequest(
        HttpMethod.POST,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        buildUrl(TAG_ID_ROUTE, tag.getId()),
        status().isNoContent());
  }

  // POST /tagCollections/{id}/tags
  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_not_creator_performs_post_tag_collections_tags_then_is_forbidden()
          throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());
    Tag tag = tagRepository.save(createTestTag());

    Mockito.when(projectClient.getCreatorIdOfProject(Mockito.any(UUID.class)))
        .thenReturn(Optional.of(UUID.randomUUID()));

    performHalRequest(
        HttpMethod.POST,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        buildUrl(TAG_ID_ROUTE, tag.getId()),
        status().isForbidden());
  }

  // POST /tagCollections/{id}/tags
  @WithMockUser(roles = {})
  @Test
  void
      when_authenticated_user_does_not_have_role_professor_performs_post_tag_collections_tags_then_is_forbidden()
          throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());

    performHalRequest(
        HttpMethod.POST,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        null,
        status().isForbidden());
  }

  // POST /tagCollections/{id}/tags
  @Test
  void when_unauthenticated_user_performs_post_tag_collections_tags_then_is_unauthorized()
      throws Exception {
    TagCollection tagCollection = tagCollectionRepository.save(createTestTagCollection());

    performHalRequest(
        HttpMethod.POST,
        buildUrl(TAG_COLLECTIONS_ID_TAGS_ROUTE, tagCollection.getReferencedEntity()),
        null,
        status().isUnauthorized());
  }
}
