package de.innovationhub.prox.tagservice.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.tagservice.tag.data.TagData;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class TagEventHandlerTest {

  private static final String TAGS_ROUTE = "/tags";
  private static final String TAGS_ID_ROUTE = "/tags/{id}";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  TagRepository tagRepository;

  @MockBean
  KafkaTemplate<String, TagData> tagDataKafkaTemplate;

  final String TAG_TOPIC = "entity.tag.tags";

  @Test
  void shouldPublishOnSave() throws Exception {
    // When
    mockMvc
      .perform(
        post(TAGS_ROUTE)
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
            {
                "tagName": "Software Architecture"
            }
            """))
      .andExpect(status().is2xxSuccessful());

    // Then
    var dataCaptor = ArgumentCaptor.forClass(TagData.class);
    verify(tagDataKafkaTemplate).send(eq(TAG_TOPIC), anyString(),
      dataCaptor.capture());
    assertThat(dataCaptor.getValue())
      .satisfies(tagData -> assertThat(tagData.getTag()).isEqualTo("software architecture"));
  }

  @Test
  void shouldPublishOnUpdate() throws Exception {
    // Given
    var tag = new Tag(new TagName("Software Architecture"));
    tagRepository.save(tag);

    // When
    mockMvc
      .perform(
        put(TAGS_ID_ROUTE, tag.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
            {
                "tagName": "Software Architecture 2"
            }
            """))
      .andExpect(status().is2xxSuccessful());

    // Then
    var dataCaptor = ArgumentCaptor.forClass(TagData.class);
    verify(tagDataKafkaTemplate).send(eq(TAG_TOPIC), anyString(),
      dataCaptor.capture());
    assertThat(dataCaptor.getValue())
      .satisfies(tagData -> assertThat(tagData.getTag()).isEqualTo("software architecture 2"));
  }

  @Test
  void handleTagDelete() throws Exception {
    // Given
    var tag = new Tag(new TagName("Software Architecture"));
    tagRepository.save(tag);

    // When
    mockMvc
      .perform(
        delete(TAGS_ID_ROUTE, tag.getId()))
      .andExpect(status().is2xxSuccessful());

    // Then
    verify(tagDataKafkaTemplate).send(eq(TAG_TOPIC), eq(tag.getId().toString()), isNull());
  }
}
