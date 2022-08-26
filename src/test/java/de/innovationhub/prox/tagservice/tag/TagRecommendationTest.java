package de.innovationhub.prox.tagservice.tag;

import static org.assertj.core.api.Assertions.assertThat;

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
class TagRecommendationTest {

  @Autowired
  TagCollectionRepository tagCollectionRepository;

  @Test
  void shouldExcludeInputTags() {
    var sampleTags = sampleTags(5);

    var tagCollection = new TagCollection(UUID.randomUUID());
    tagCollection.setTags(sampleTags);
    tagCollectionRepository.save(tagCollection);

    var tagInput = sampleTags.subList(0, 2);
    var inputTags = tagInput
        .stream().map(Tag::getTag)
        .toList();
    var tagRecommendation = tagCollectionRepository.tagRecommendations(inputTags);

    assertThat(tagRecommendation)
        .hasSizeGreaterThanOrEqualTo(1)
        .doesNotContainAnyElementsOf(tagInput);
  }

  @Test
  void shouldNotContainDuplicateTags() {
    var sampleTags = sampleTags(5);

    var tagCollection1 = new TagCollection(UUID.randomUUID());
    var tagCollection2 = new TagCollection(UUID.randomUUID());
    tagCollection1.setTags(sampleTags);
    tagCollection2.setTags(sampleTags);
    tagCollectionRepository.save(tagCollection1);
    tagCollectionRepository.save(tagCollection2);

    var tagInput = sampleTags.subList(0, 2);
    var inputTags = tagInput
        .stream().map(Tag::getTag)
        .toList();
    var tagRecommendation = tagCollectionRepository.tagRecommendations(inputTags);

    assertThat(tagRecommendation)
        .hasSizeGreaterThanOrEqualTo(1)
        .doesNotHaveDuplicates();
  }

  @Test
  void shouldSortByFrequency() {
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

    var tagRecommendation = tagCollectionRepository.tagRecommendations(List.of(fourth.getTag()));

    assertThat(tagRecommendation)
        .containsExactly(third, second, first);
  }

  @Test
  void shouldRecommendTagsInOtherCollection() {
    var sampleTags = sampleTags(3);

    var tagCollection1 = new TagCollection(UUID.randomUUID());
    var tagCollection2 = new TagCollection(UUID.randomUUID());

    var first =  sampleTags.get(0);
    var second =  sampleTags.get(1);
    var third =  sampleTags.get(2);

    tagCollection1.setTags(Set.of(first, second));
    tagCollection2.setTags(Set.of(first, third));

    tagCollectionRepository.save(tagCollection1);
    tagCollectionRepository.save(tagCollection2);

    var tagRecommendation = tagCollectionRepository.tagRecommendations(List.of(first.getTag()));

    assertThat(tagRecommendation)
        .contains(second, third);
  }

  private List<Tag> sampleTags(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> new Tag("Tag_" + i))
        .toList();
  }
}
