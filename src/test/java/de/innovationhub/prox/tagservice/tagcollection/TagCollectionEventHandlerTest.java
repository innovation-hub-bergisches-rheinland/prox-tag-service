package de.innovationhub.prox.tagservice.tagcollection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagName;
import de.innovationhub.prox.tagservice.tag.TagRepository;
import de.innovationhub.prox.tagservice.tag.data.TagCollectionData;
import de.innovationhub.prox.tagservice.tag.data.TagData;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
class TagCollectionEventHandlerTest {
  private static final String TAG_COLLECTIONS_ROUTE = "/tagCollections";
  private static final String TAG_COLLECTIONS_ID_ROUTE = "/tagCollections/{id}";
  private static final String TAG_COLLECTIONS_ID_TAGS_ROUTE = "/tagCollections/{id}/tags";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  TagCollectionRepository tagCollectionRepository;

  @Autowired
  EntityManager entityManager;

  @MockBean
  KafkaTemplate<String, TagCollectionData> tagCollectionDataKafkaTemplate;

  final String TAG_COLLECTIONS_TOPIC = "entity.tag.collections";

  @Test
  void shouldPublishOnDelete() throws Exception {
    UUID projectId = UUID.randomUUID();
    Tag tag1 = new Tag(new TagName("Tag1"));
    Tag tag2 = new Tag(new TagName("Tag2"));

    entityManager.persist(tag1);
    entityManager.persist(tag2);

    TagCollection tagCollection = new TagCollection(projectId, Set.of(tag1, tag2));
    tagCollectionRepository.save(tagCollection);

    mockMvc
      .perform(delete(TAG_COLLECTIONS_ID_ROUTE, tagCollection.getReferencedEntity()))
      .andExpect(status().is2xxSuccessful());

    verify(tagCollectionDataKafkaTemplate).send(eq(TAG_COLLECTIONS_TOPIC), eq(tagCollection.getReferencedEntity().toString()), isNull());
  }

  @Test
  void shouldPublishOnPut() throws Exception {
    UUID referencedEntityId = UUID.randomUUID();
    TagCollection tagCollection = new TagCollection(referencedEntityId);
    tagCollectionRepository.save(tagCollection);

    Tag tag1 = new Tag(new TagName("Tag1"));
    Tag tag2 = new Tag(new TagName("Tag2"));

    entityManager.persist(tag1);
    entityManager.persist(tag2);

    mockMvc
      .perform(
        put(TAG_COLLECTIONS_ID_TAGS_ROUTE, referencedEntityId)
          .contentType("text/uri-list")
          .characterEncoding("UTF-8")
          .content("%s\n%s".formatted(tag1.getId(), tag2.getId())))
      .andExpect(status().is2xxSuccessful());

    var dataCaptor = ArgumentCaptor.forClass(TagCollectionData.class);
    verify(tagCollectionDataKafkaTemplate).send(eq(TAG_COLLECTIONS_TOPIC), eq(referencedEntityId.toString()), dataCaptor.capture());
    assertThat(dataCaptor.getValue())
      .satisfies(tagCollectionData -> {
        assertThat(tagCollectionData.getId()).isEqualTo(referencedEntityId.toString());
        assertThat(tagCollectionData.getTagsList())
          .extracting(TagData::getId, TagData::getTag)
          .containsExactlyInAnyOrder(
            tuple(tag1.getId().toString(), tag1.getTagName().getTagName()),
            tuple(tag2.getId().toString(), tag2.getTagName().getTagName())
          );
      });
  }

  @Test
  void shouldPublishOnPost() throws Exception {
    UUID referencedEntityId = UUID.randomUUID();
    TagCollection tagCollection = new TagCollection(referencedEntityId);
    tagCollectionRepository.save(tagCollection);

    Tag tag1 = new Tag(new TagName("Tag1"));
    Tag tag2 = new Tag(new TagName("Tag2"));

    entityManager.persist(tag1);
    entityManager.persist(tag2);

    mockMvc
      .perform(
        post(TAG_COLLECTIONS_ID_TAGS_ROUTE, referencedEntityId)
          .contentType("text/uri-list")
          .characterEncoding("UTF-8")
          .content("%s\n%s".formatted(tag1.getId(), tag2.getId())))
      .andExpect(status().is2xxSuccessful());

    var dataCaptor = ArgumentCaptor.forClass(TagCollectionData.class);
    verify(tagCollectionDataKafkaTemplate).send(eq(TAG_COLLECTIONS_TOPIC), eq(referencedEntityId.toString()), dataCaptor.capture());
    assertThat(dataCaptor.getValue())
      .satisfies(tagCollectionData -> {
        assertThat(tagCollectionData.getId()).isEqualTo(referencedEntityId.toString());
        assertThat(tagCollectionData.getTagsList())
          .extracting(TagData::getId, TagData::getTag)
          .containsExactlyInAnyOrder(
            tuple(tag1.getId().toString(), tag1.getTagName().getTagName()),
            tuple(tag2.getId().toString(), tag2.getTagName().getTagName())
          );
      });
  }

}
