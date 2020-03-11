package io.archilab.prox.tagservice.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.archilab.prox.tagservice.tag.recommendation.TagCounter;
import io.archilab.prox.tagservice.tag.recommendation.TagCounterRepository;
import io.archilab.prox.tagservice.tag.recommendation.TagRecommendationCalculator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest
@ComponentScan
public class TagRecommendationCalculatorTest {

  @Autowired private TagCounterRepository tagCounterRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private TagRecommendationCalculator tagRecommendationCalculator;

  @Test
  public void testTagRecommendations() {
    // Create Tags and TagCounter
    Tag tag1 = tagRepository.save(new Tag(new TagName("Microservices")));
    Tag tag2 = tagRepository.save(new Tag(new TagName("FAE")));
    Tag tag3 = tagRepository.save(new Tag(new TagName("DDD")));
    Tag tag4 = tagRepository.save(new Tag(new TagName("REST")));
    Tag tag5 = tagRepository.save(new Tag(new TagName("Monolith")));

    tagCounterRepository.save(new TagCounter(tag1, tag2, 4));
    tagCounterRepository.save(new TagCounter(tag1, tag3, 3));
    tagCounterRepository.save(new TagCounter(tag1, tag4, 5));
    tagCounterRepository.save(new TagCounter(tag2, tag3, 30));
    tagCounterRepository.save(new TagCounter(tag3, tag4, 3));

    List<Tag> result =
        tagRecommendationCalculator.getRecommendedTags(new UUID[] {tag1.getId(), tag2.getId()});
    assertEquals(result.size(), 2);
    assertEquals(result.get(0), tag3);
    assertEquals(result.get(1), tag4);
  }
}
