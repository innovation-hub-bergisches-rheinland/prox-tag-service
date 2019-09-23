package io.archilab.prox.tagservice.tag;

import io.archilab.prox.tagservice.tag.recommendation.TagCounterRepository;
import io.archilab.prox.tagservice.tag.recommendation.TagCounterUpdater;
import lombok.var;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class TagCounterTest {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TagCounterRepository tagCounterRepository;

  @Autowired
  private TagCollectionRepository tagCollectionRepository;

  @Test
  public void tagCounterTest() {
    var tags = this.createTags(5);

    this.createCollection(tags.get(0), tags.get(1), tags.get(2), tags.get(3), tags.get(4));
    this.createCollection(tags.get(0), tags.get(1), tags.get(2), tags.get(3));
    this.createCollection(tags.get(0), tags.get(1), tags.get(2));
    this.createCollection(tags.get(0), tags.get(1));

    var updater = new TagCounterUpdater(this.tagCollectionRepository, this.tagCounterRepository);
    updater.updateTagCounter();

    this.validateCounter(tags.get(0), tags.get(1), 4);
    this.validateCounter(tags.get(0), tags.get(2), 3);
    this.validateCounter(tags.get(0), tags.get(3), 2);
    this.validateCounter(tags.get(0), tags.get(4), 1);

    this.validateCounter(tags.get(1), tags.get(0), 4);
    this.validateCounter(tags.get(1), tags.get(2), 3);
    this.validateCounter(tags.get(1), tags.get(3), 2);
    this.validateCounter(tags.get(1), tags.get(4), 1);

    this.validateCounter(tags.get(2), tags.get(0), 3);
    this.validateCounter(tags.get(2), tags.get(1), 3);
    this.validateCounter(tags.get(2), tags.get(3), 2);
    this.validateCounter(tags.get(2), tags.get(4), 1);

    this.validateCounter(tags.get(3), tags.get(0), 2);
    this.validateCounter(tags.get(3), tags.get(1), 2);
    this.validateCounter(tags.get(3), tags.get(2), 2);
    this.validateCounter(tags.get(3), tags.get(4), 1);

    this.validateCounter(tags.get(4), tags.get(0), 1);
    this.validateCounter(tags.get(4), tags.get(1), 1);
    this.validateCounter(tags.get(4), tags.get(2), 1);
    this.validateCounter(tags.get(4), tags.get(3), 1);
  }

  private void validateCounter(Tag tag1, Tag tag2, int count) {
    var result = Lists.newArrayList(this.tagCounterRepository.findAll());

    var counter = result.stream().filter(f -> (f.getTag1() == tag1 && f.getTag2() == tag2)
        || (f.getTag1() == tag2 && f.getTag2() == tag1)).findFirst().get();

    Assert.assertEquals(count, counter.getCount());
  }


  private List<Tag> createTags(int count) {

    List<Tag> tags = new ArrayList<>();

    for (int i = 1; i <= count; i++)
      tags.add(new Tag(new TagName("Tag " + i)));

    return Lists.newArrayList(this.tagRepository.saveAll(tags));
  }

  private TagCollection createCollection(Tag... tag) {
    TagCollection col = new TagCollection(UUID.randomUUID());

    for (Tag t : tag)
      col.addTag(t);

    return this.tagCollectionRepository.save(col);
  }

}
