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

package io.archilab.prox.tagservice.tag.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.archilab.prox.tagservice.tag.Tag;
import io.archilab.prox.tagservice.tag.TagName;
import io.archilab.prox.tagservice.tag.TagRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.util.UriComponentsBuilder;


public class TagRouteSecurityTest extends RouteSecurityTest {

  private static final String TAGS_ROUTE = "/tags";
  private static final String TAGS_ID_ROUTE = "/tags/{id}";

  @Autowired
  TagRepository tagRepository;

  @BeforeEach
  @Override
  void init() {
    super.init();
    tagRepository.deleteAll();
  }

  private Tag createTestTag() {
    return new Tag(
        new TagName("Test Tag")
    );
  }

  //POST /tags
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_post_tag_then_is_created() throws Exception {
    Tag tag = createTestTag();

    performRequest(HttpMethod.POST, TAGS_ROUTE, tag, status().isCreated());
  }

  //POST /tags
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_post_tag_then_is_forbidden() throws Exception {
    Tag tag = createTestTag();

    performRequest(HttpMethod.POST, TAGS_ROUTE, tag, status().isForbidden());
  }

  //POST /tags
  @Test
  void when_unauthenticated_user_performs_post_tag_then_is_unauthorized() throws Exception {
    Tag tag = createTestTag();

    performRequest(HttpMethod.POST, TAGS_ROUTE, tag, status().isUnauthorized());
  }

  //PUT /tags/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_put_tag_then_is_ok() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PUT, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isOk());
  }

  //PUT /tags/{id}
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_put_tag_then_is_forbidden() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PUT, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isForbidden());
  }

  //PUT /tags/{id}
  @Test
  void when_unauthenticated_user_performs_put_tag_then_is_unauthorized() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PUT, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isUnauthorized());
  }

  //PATCH /tags/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_patch_tag_then_is_ok() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PATCH, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isOk());
  }

  //PATCH /tags/{id}
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_patch_tag_then_is_forbidden() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PATCH, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isForbidden());
  }

  //PATCH /tags/{id}
  @Test
  void when_unauthenticated_user_performs_patch_tag_then_is_unauthorized() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.PATCH, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isUnauthorized());
  }

  //DELETE /tags/{id}
  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_delete_tag_then_is_no_content() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.DELETE, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isNoContent());
  }

  //DELETE /tags/{id}
  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_have_role_professor_performs_delete_tag_then_is_forbidden() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.DELETE, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isForbidden());
  }

  //DELETE /tags/{id}
  @Test
  void when_unauthenticated_user_performs_delete_tag_then_is_unauthorized() throws Exception {
    Tag tag = tagRepository.save(createTestTag());

    performRequest(HttpMethod.DELETE, buildUrl(TAGS_ID_ROUTE, tag.getId()), tag, status().isUnauthorized());
  }

  //GET /tags
  @Test
  void when_unauthenticated_user_performs_get_tags_then_is_ok() throws Exception {
    performRequest(HttpMethod.GET, TAGS_ROUTE, null, status().isOk());
  }

}
