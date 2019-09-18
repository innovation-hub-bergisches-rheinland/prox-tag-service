package io.archilab.prox.tagservice.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import io.archilab.prox.tagservice.tag.recommendation.TagCounter;
import io.archilab.prox.tagservice.tag.recommendation.TagCounterRepository;

import java.util.UUID;

@Component
public class TagSamplesGenerator {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TagCounterRepository tagCounterRepository;

  @Autowired
  private TagCollectionRepository tagCollectionRepository;


  @PostConstruct
  private void generateSampleData() {
    Tag tag1 = tagRepository.save(new Tag(new TagName("Microservices")));
    Tag tag2 = tagRepository.save(new Tag(new TagName("FAE")));
    Tag tag3 = tagRepository.save(new Tag(new TagName("DDD")));
    Tag tag4 = tagRepository.save(new Tag(new TagName("REST")));
    Tag tag5 = tagRepository.save(new Tag(new TagName("Monolith")));

    createCollection(tag1, tag2, tag3);
    createCollection(tag1, tag3, tag5);
    createCollection(tag3, tag4, tag2);
    createCollection(tag3, tag4, tag1);

    tagCounterRepository.save(new TagCounter(tag1, tag2, 3));
    tagCounterRepository.save(new TagCounter(tag1, tag3, 3));
    tagCounterRepository.save(new TagCounter(tag1, tag4, 3));
    tagCounterRepository.save(new TagCounter(tag2, tag3, 3));
    tagCounterRepository.save(new TagCounter(tag3, tag4, 3));
  }

  private void createCollection(Tag tag1, Tag tag2, Tag tag3) {
    TagCollection col1 = new TagCollection(UUID.randomUUID());
    col1.addTag(tag1);
    col1.addTag(tag2);
    col1.addTag(tag3);
    this.tagCollectionRepository.save(col1);
  }
}
