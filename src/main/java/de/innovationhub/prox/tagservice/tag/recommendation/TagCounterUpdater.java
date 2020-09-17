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

package de.innovationhub.prox.tagservice.tag.recommendation;

import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class TagCounterUpdater {

  private final Logger log = LoggerFactory.getLogger(TagCounterUpdater.class);

  @Autowired private TagCounterRepository tagCounterRepository;

  public TagCounterUpdater(TagCounterRepository tagCounterRepository) {
    this.tagCounterRepository = tagCounterRepository;
  }

  public void updateTagCounter(TagCollection updatedTagCollection, List<Tag> oldTags) {
    // decrease counts of tag combinations which are now obsolete because of an updated association
    if (oldTags != null && !oldTags.isEmpty()) {
      applyTagCountersToRepository(getTagCounterFromTags(oldTags), false);
    }

    // increase counts of tag combinations which are new
    applyTagCountersToRepository(getTagCounterFromTags(updatedTagCollection.getTags()), true);
  }

  private void applyTagCountersToRepository(List<TagCounter> tagCounters, boolean additive) {
    for (TagCounter tagCounter : tagCounters) {
      TagCounter existingTagCounter = getTagCounterFromRepository(tagCounter);
      if (existingTagCounter == null) {
        if (additive) {
          tagCounterRepository.save(tagCounter);
        }
      } else {
        if (additive) {
          existingTagCounter.setCount(existingTagCounter.getCount() + 1);
        } else {
          existingTagCounter.setCount(existingTagCounter.getCount() - 1);
        }
        tagCounterRepository.save(existingTagCounter);
      }
    }
  }

  private TagCounter getTagCounterFromRepository(TagCounter tagCounter) {
    Optional<TagCounter> existingTagCounterOpt =
        tagCounterRepository.findByTag1AndTag2(tagCounter.getTag1(), tagCounter.getTag2());
    if (!existingTagCounterOpt.isPresent()) {
      existingTagCounterOpt =
          tagCounterRepository.findByTag1AndTag2(tagCounter.getTag2(), tagCounter.getTag1());
    }
    return existingTagCounterOpt.isPresent() ? existingTagCounterOpt.get() : null;
  }

  private List<TagCounter> getTagCounterFromTags(List<Tag> tags) {
    List<TagCounter> tagCounters = new ArrayList<>();

    for (int i = 0; i < tags.size() - 1; i++) {
      for (int k = i + 1; k < tags.size(); k++) {
        TagCounter counter = new TagCounter(tags.get(i), tags.get(k), 1);

        if (!tagCounters.contains(counter)) {
          tagCounters.add(counter);
        }
      }
    }

    return tagCounters;
  }
}
