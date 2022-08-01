package de.innovationhub.prox.tagservice.tag.recommendation;


import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagRepository;
import java.util.*;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class TagRecommendationCalculator {

  private TagCounterRepository tagCounterRepository;
  private TagRepository tagRepository;

  @Autowired
  @Lazy
  public TagRecommendationCalculator(
      TagCounterRepository tagCounterRepository, TagRepository tagRepository) {
    this.tagCounterRepository = tagCounterRepository;
    this.tagRepository = tagRepository;
  }

  @Value("${tagRecommendationCalculation.resultCount}")
  private int resultCount;

  public List<Tag> getRecommendedTags(Collection<UUID> tagIds) {
    // Retrieve search tags
    List<Tag> searchTags = tagRepository.findByIdIn(tagIds);

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
