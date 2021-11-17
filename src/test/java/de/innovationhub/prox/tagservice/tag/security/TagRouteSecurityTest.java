package de.innovationhub.prox.tagservice.tag.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagName;
import de.innovationhub.prox.tagservice.tag.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;

public class TagRouteSecurityTest extends RouteSecurityTest {

  private static final String TAGS_ROUTE = "/tags";
  private static final String TAGS_ID_ROUTE = "/tags/{id}";

  @Autowired TagRepository tagRepository;

  @BeforeEach
  @Override
  void init() {
    super.init();
    tagRepository.deleteAll();
  }

  private Tag createTestTag() {
    return new Tag(new TagName("Test Tag"));
  }

  // POST /tags
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_post_tag_then_is_created()
      throws Exception {
    Tag tag = createTestTag();

    performRequest(HttpMethod.POST, TAGS_ROUTE, tag, status().isCreated());
  }

  // POST /tags
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_post_tag_then_is_forbidden()
      throws Exception {
    Tag tag = createTestTag();

    performRequest(HttpMethod.POST, TAGS_ROUTE, tag, status().isForbidden());
  }

  // POST /tags
  @Test
  void when_unauthenticated_user_performs_post_tag_then_is_unauthorized() throws Exception {
    Tag tag = createTestTag();

    performRequest(HttpMethod.POST, TAGS_ROUTE, tag, status().isUnauthorized());
  }

  // PUT /tags/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_put_tag_then_is_ok() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PUT, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isOk());
  }

  // PUT /tags/{id}
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_put_tag_then_is_forbidden()
      throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(
        HttpMethod.PUT, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isForbidden());
  }

  // PUT /tags/{id}
  @Test
  void when_unauthenticated_user_performs_put_tag_then_is_unauthorized() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(
        HttpMethod.PUT, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isUnauthorized());
  }

  // PATCH /tags/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_patch_tag_then_is_ok() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PATCH, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isOk());
  }

  // PATCH /tags/{id}
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_patch_tag_then_is_forbidden()
      throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(
        HttpMethod.PATCH, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isForbidden());
  }

  // PATCH /tags/{id}
  @Test
  void when_unauthenticated_user_performs_patch_tag_then_is_unauthorized() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(
        HttpMethod.PATCH, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isUnauthorized());
  }

  // DELETE /tags/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_delete_tag_then_is_no_content()
      throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(
        HttpMethod.DELETE, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isNoContent());
  }

  // DELETE /tags/{id}
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_delete_tag_then_is_forbidden()
      throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(
        HttpMethod.DELETE, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isForbidden());
  }

  // DELETE /tags/{id}
  @Test
  void when_unauthenticated_user_performs_delete_tag_then_is_unauthorized() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(
        HttpMethod.DELETE, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isUnauthorized());
  }

  // GET /tags
  @Test
  void when_unauthenticated_user_performs_get_tags_then_is_ok() throws Exception {
    performRequest(HttpMethod.GET, TAGS_ROUTE, null, status().isOk());
  }
}
