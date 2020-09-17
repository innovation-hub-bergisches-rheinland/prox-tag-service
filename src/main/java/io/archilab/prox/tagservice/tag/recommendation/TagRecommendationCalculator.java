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

package io.archilab.prox.tagservice.tag.recommendation;

import io.archilab.prox.tagservice.tag.Tag;
import io.archilab.prox.tagservice.tag.TagRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TagRecommendationCalculator {

  @Autowired private TagCounterRepository tagCounterRepository;

  @Autowired private TagRepository tagRepository;

  @Value("${tagRecommendationCalculation.resultCount}")
  private int resultCount;

  public List<Tag> getRecommendedTags(UUID[] tagIds) {
    // Retrieve search tags
    List<Tag> searchTags = new ArrayList<>();
    for (UUID tagId : tagIds) {
      Optional<Tag> optionalSearchTag = tagRepository.findById(tagId);
      if (optionalSearchTag.isPresent()) {
        searchTags.add(optionalSearchTag.get());
      }
    }

    if (searchTags.isEmpty()) {
      return new ArrayList<>();
    }

    // Score Tags
    Map<Tag, Integer> recommendedTags = new HashMap<>();
    for (Tag searchTag : searchTags) {
      List<TagCounter> tagCounters = tagCounterRepository.findByTag1OrTag2(searchTag, searchTag);
      for (TagCounter tagCounter : tagCounters) {
        Tag otherTag = tagCounter.getOtherTag(searchTag);
        if (!searchTags.contains(otherTag)) {
          Integer count = recommendedTags.get(otherTag);
          if (count == null) {
            recommendedTags.put(otherTag, tagCounter.getCount());
          } else {
            recommendedTags.put(otherTag, count + tagCounter.getCount());
          }
        }
      }
    }

    // Sort recommended tags
    List<Entry<Tag, Integer>> sortedRecommendedTags = new ArrayList<>(recommendedTags.entrySet());
    sortedRecommendedTags.sort(
        new Comparator<Entry<Tag, Integer>>() {
          @Override
          public int compare(Entry<Tag, Integer> a, Entry<Tag, Integer> b) {
            return a.getValue() > b.getValue() ? -1 : a.getValue() == b.getValue() ? 0 : 1;
          }
        });

    List<Tag> returnedRecommendedTags = new ArrayList<>();
    for (int i = 0; i < resultCount && i < sortedRecommendedTags.size(); i++) {
      returnedRecommendedTags.add(sortedRecommendedTags.get(i).getKey());
    }
    return returnedRecommendedTags;
  }
}
