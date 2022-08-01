package de.innovationhub.prox.tagservice.tag.recommendation;


import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagRepository;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
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
    Map<Tag, Integer> recommendedTags = searchTags.stream()
        .flatMap(tag ->
            tagCounterRepository.findByTag1OrTag2(tag, tag)
                .stream()
                .filter(tagCounter -> !searchTags.contains(tagCounter.getOtherTag(tag)))
                .map(tagCounter -> Map.entry(tagCounter.getOtherTag(tag), tagCounter.getCount()))
        )
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, Integer::sum));

    // Sort recommended tags
    List<Entry<Tag, Integer>> sortedRecommendedTags = recommendedTags.entrySet().stream()
        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
        .toList();

    return sortedRecommendedTags
        .stream().limit(resultCount)
        .map(Entry::getKey)
        .toList();
  }
}
