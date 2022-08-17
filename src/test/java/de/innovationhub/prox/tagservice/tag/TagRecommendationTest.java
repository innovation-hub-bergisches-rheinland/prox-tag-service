package de.innovationhub.prox.tagservice.tag;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.tagservice.tagcollection.TagCollection;
import de.innovationhub.prox.tagservice.tagcollection.TagCollectionRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TagRecommendationTest {
  @Autowired
  TagRepository tagRepository;

  @Autowired
  TagCollectionRepository tagCollectionRepository;

  @Test
  void shouldExcludeInputTags() {
    var sampleTags = sampleTags(5);
    tagRepository.saveAll(sampleTags);

    var tagCollection = new TagCollection(UUID.randomUUID());
    tagCollection.addTags(sampleTags);
    tagCollectionRepository.save(tagCollection);

    var tagInput = sampleTags.subList(0, 2);
    var inputIds = tagInput
        .stream().map(Tag::getId)
        .toList();
    var tagRecommendation = tagRepository.tagRecommendations(inputIds);

    assertThat(tagRecommendation)
        .hasSizeGreaterThanOrEqualTo(1)
        .doesNotContainAnyElementsOf(tagInput);
  }

  @Test
  void shouldNotContainDuplicateTags() {
    var sampleTags = sampleTags(5);
    tagRepository.saveAll(sampleTags);

    var tagCollection1 = new TagCollection(UUID.randomUUID());
    var tagCollection2 = new TagCollection(UUID.randomUUID());
    tagCollection1.addTags(sampleTags);
    tagCollection2.addTags(sampleTags);
    tagCollectionRepository.save(tagCollection1);
    tagCollectionRepository.save(tagCollection2);

    var tagInput = sampleTags.subList(0, 2);
    var inputIds = tagInput
        .stream().map(Tag::getId)
        .toList();
    var tagRecommendation = tagRepository.tagRecommendations(inputIds);

    assertThat(tagRecommendation)
        .hasSizeGreaterThanOrEqualTo(1)
        .doesNotHaveDuplicates();
  }

  @Test
  void shouldSortByFrequency() {
    var sampleTags = sampleTags(4);
    tagRepository.saveAll(sampleTags);

    var tagCollection1 = new TagCollection(UUID.randomUUID());
    var tagCollection2 = new TagCollection(UUID.randomUUID());
    var tagCollection3 = new TagCollection(UUID.randomUUID());

    var first =  sampleTags.get(0);
    var second =  sampleTags.get(1);
    var third =  sampleTags.get(2);
    var fourth =  sampleTags.get(3);

    // This will be the search tag
    tagCollection1.addTag(fourth);
    tagCollection2.addTag(fourth);
    tagCollection3.addTag(fourth);

    tagCollection1.addTag(third);
    tagCollection2.addTag(third);
    tagCollection3.addTag(third);

    tagCollection2.addTag(second);
    tagCollection3.addTag(second);

    tagCollection1.addTag(first);

    tagCollectionRepository.save(tagCollection1);
    tagCollectionRepository.save(tagCollection2);
    tagCollectionRepository.save(tagCollection3);

    var tagRecommendation = tagRepository.tagRecommendations(List.of(fourth.getId()));

    assertThat(tagRecommendation)
        .containsExactly(third, second, first);
  }

  @Test
  void shouldRecommendTagsInOtherCollection() {
    var sampleTags = sampleTags(3);
    tagRepository.saveAll(sampleTags);

    var tagCollection1 = new TagCollection(UUID.randomUUID());
    var tagCollection2 = new TagCollection(UUID.randomUUID());

    var first =  sampleTags.get(0);
    var second =  sampleTags.get(1);
    var third =  sampleTags.get(2);

    tagCollection1.addTag(first);

    tagCollection2.addTag(first);
    tagCollection1.addTag(second);
    tagCollection2.addTag(third);

    tagCollectionRepository.save(tagCollection1);
    tagCollectionRepository.save(tagCollection2);

    var tagRecommendation = tagRepository.tagRecommendations(List.of(first.getId()));

    assertThat(tagRecommendation)
        .contains(second, third);
  }

  private List<Tag> sampleTags(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> new Tag(new TagName("Tag_" + i)))
        .toList();
  }
}
