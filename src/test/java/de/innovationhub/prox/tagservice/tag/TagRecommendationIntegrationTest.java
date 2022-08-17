package de.innovationhub.prox.tagservice.tag;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.innovationhub.prox.tagservice.tagcollection.TagCollection;
import de.innovationhub.prox.tagservice.tagcollection.TagCollectionRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@Transactional
class TagRecommendationIntegrationTest {
  @Autowired
  TagRepository tagRepository;

  @Autowired
  TagCollectionRepository tagCollectionRepository;

  @Autowired
  MockMvc mockMvc;

  @Test
  void shouldGetRecommendations() throws Exception {
    // Given
    var sampleTags = sampleTags(5);
    tagRepository.saveAll(sampleTags);

    var tagCollection1 = new TagCollection(UUID.randomUUID());
    var tagCollection2 = new TagCollection(UUID.randomUUID());

    tagCollection1.addTags(sampleTags.subList(0, 3));
    tagCollection2.addTags(sampleTags.subList(1, 5));

    tagCollectionRepository.save(tagCollection1);
    tagCollectionRepository.save(tagCollection2);

    var tagInput = sampleTags.subList(1, 3).stream().map(Tag::getId).toList();

    var expectedTags = Stream.concat(sampleTags.subList(0, 1).stream(), sampleTags.subList(3, 5).stream()).toList();
    var expectedTagIds = expectedTags.stream().map(t -> t.getId().toString()).toList();

    mockMvc.perform(get("/tags/search/tagRecommendations?tagIds={tagIds}",
        String.join(",", tagInput.stream().map(Object::toString).toArray(String[]::new))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.tags", hasSize(3)))
        .andExpect(jsonPath("$._embedded.tags[*].id", containsInAnyOrder(expectedTagIds.toArray())));
  }

  private List<Tag> sampleTags(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> new Tag(new TagName("Tag_" + i)))
        .toList();
  }
}
