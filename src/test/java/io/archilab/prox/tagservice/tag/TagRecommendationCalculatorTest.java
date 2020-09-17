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
