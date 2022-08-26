package de.innovationhub.prox.tagservice.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("h2")
class TagPopularityTest {

  @Autowired
  TagCollectionRepository tagCollectionRepository;

  @Test
  void shouldReturnEmptyMap() {
    var popularTags = tagCollectionRepository.findPopularTags(10);

    assertThat(popularTags)
      .isEmpty();
  }

  @Test
  void shouldCountTags() {
    var sampleTags = sampleTags(4);

    var tagCollection1 = new TagCollection(UUID.randomUUID());
    var tagCollection2 = new TagCollection(UUID.randomUUID());
    var tagCollection3 = new TagCollection(UUID.randomUUID());

    var first =  sampleTags.get(0);
    var second =  sampleTags.get(1);
    var third =  sampleTags.get(2);
    var fourth =  sampleTags.get(3);

    // This will be the search tags
    tagCollection1.setTags(Set.of(first, third, fourth));
    tagCollection2.setTags(Set.of(second, third, fourth));
    tagCollection3.setTags(Set.of(second, third, fourth));

    tagCollectionRepository.save(tagCollection1);
    tagCollectionRepository.save(tagCollection2);
    tagCollectionRepository.save(tagCollection3);

    var popularTags = tagCollectionRepository.findPopularTags(10);

    assertThat(popularTags)
      .containsOnly(
        entry(first.getTag(), 1),
        entry(second.getTag(), 2),
        entry(third.getTag(), 3),
        entry(fourth.getTag(), 3)
      );
  }

  private List<Tag> sampleTags(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> new Tag("Tag_" + i))
        .toList();
  }
}
