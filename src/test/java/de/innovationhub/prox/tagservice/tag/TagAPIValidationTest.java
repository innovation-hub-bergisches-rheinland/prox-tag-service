package de.innovationhub.prox.tagservice.tag;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.tagservice.utils.AuthenticationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@Transactional
public class TagAPIValidationTest {

  private static final String TAG_ROUTE = "/tags";
  private static final String TAG_ID_ROUTE = "/tags/{id}";

  @Autowired MockMvc mockMvc;

  @Autowired TagRepository tagRepository;

  @MockBean AuthenticationUtils authenticationUtils;

  Tag emptyTag = new Tag();

  Tag nullValueTag = new Tag(null);

  Tag emptyValueTag = new Tag(new TagName("  "));

  Tag zwspValueTag = new Tag(new TagName("a\u200Bb"));

  Tag validTag = new Tag(new TagName("Tag 1"));

  @Test
  void when_post_empty_body_then_is_bad_request() throws Exception {
    mockMvc
        .perform(post(TAG_ROUTE).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_empty_tag_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(TAG_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_null_tag_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(TAG_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(nullValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_empty_value_tag_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(TAG_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_zwsp_value_tag_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(TAG_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(zwspValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_empty_body_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            put(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_empty_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            put(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_null_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            put(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(nullValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_empty_value_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            put(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_zwsp_value_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            put(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(zwspValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_empty_body_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            patch(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_empty_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            patch(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_null_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            patch(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(nullValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_empty_value_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            patch(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_zwsp_value_tag_then_is_bad_request() throws Exception {
    Tag savedTag = tagRepository.save(validTag);

    mockMvc
        .perform(
            patch(TAG_ID_ROUTE, savedTag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(zwspValueTag)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }
}
