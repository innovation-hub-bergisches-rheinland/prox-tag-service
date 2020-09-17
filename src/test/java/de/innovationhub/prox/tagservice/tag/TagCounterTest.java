/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.tagservice.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.innovationhub.prox.tagservice.tag.recommendation.TagCounterRepository;
import de.innovationhub.prox.tagservice.tag.recommendation.TagCounterUpdater;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.testcontainers.shaded.com.google.common.collect.Lists;

@DataJpaTest
@ComponentScan
public class TagCounterTest {

  @Autowired private TagRepository tagRepository;

  @Autowired private TagCounterRepository tagCounterRepository;

  @Autowired private TagCollectionRepository tagCollectionRepository;

  @Test
  public void tagCounterTest() {
    var updater = new TagCounterUpdater(this.tagCounterRepository);

    var tags = this.createTags(5);

    TagCollection tagCollection1 =
        this.createCollection(tags.get(0), tags.get(1), tags.get(2), tags.get(3), tags.get(4));
    TagCollection tagCollection2 =
        this.createCollection(tags.get(0), tags.get(1), tags.get(2), tags.get(3));
    TagCollection tagCollection3 = this.createCollection(tags.get(0), tags.get(1), tags.get(2));
    TagCollection tagCollection4 = this.createCollection(tags.get(0), tags.get(1));

    updater.updateTagCounter(tagCollection1, null);
    updater.updateTagCounter(tagCollection2, null);
    updater.updateTagCounter(tagCollection3, null);
    updater.updateTagCounter(tagCollection4, null);

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

    var counter =
        result.stream()
            .filter(
                f ->
                    (f.getTag1() == tag1 && f.getTag2() == tag2)
                        || (f.getTag1() == tag2 && f.getTag2() == tag1))
            .findFirst()
            .orElseThrow();

    assertEquals(count, counter.getCount());
  }

  private List<Tag> createTags(int count) {

    List<Tag> tags = new ArrayList<>();

    for (int i = 1; i <= count; i++) {
      tags.add(new Tag(new TagName("Tag " + i)));
    }

    return Lists.newArrayList(this.tagRepository.saveAll(tags));
  }

  private TagCollection createCollection(Tag... tag) {
    TagCollection col = new TagCollection(UUID.randomUUID());

    for (Tag t : tag) {
      col.addTag(t);
    }

    return this.tagCollectionRepository.save(col);
  }
}
