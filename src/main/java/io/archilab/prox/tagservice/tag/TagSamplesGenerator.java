package io.archilab.prox.tagservice.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import io.archilab.prox.tagservice.tag.recommendation.TagCounter;
import io.archilab.prox.tagservice.tag.recommendation.TagCounterRepository;

@Component
public class TagSamplesGenerator {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TagCounterRepository tagCounterRepository;


  @PostConstruct
  private void generateSampleData() {
    Tag tag1 = tagRepository.save(new Tag(new TagName("Microservices")));
    Tag tag2 = tagRepository.save(new Tag(new TagName("FAE")));
    Tag tag3 = tagRepository.save(new Tag(new TagName("DDD")));
    Tag tag4 = tagRepository.save(new Tag(new TagName("REST")));
    Tag tag5 = tagRepository.save(new Tag(new TagName("Monolith")));

    tagCounterRepository.save(new TagCounter(tag1, tag2, 3));
    tagCounterRepository.save(new TagCounter(tag1, tag3, 3));
    tagCounterRepository.save(new TagCounter(tag1, tag4, 3));
    tagCounterRepository.save(new TagCounter(tag2, tag3, 3));
    tagCounterRepository.save(new TagCounter(tag3, tag4, 3));
  }
}
