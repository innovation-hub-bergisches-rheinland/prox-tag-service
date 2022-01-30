package de.innovationhub.prox.tagservice.tag;


import de.innovationhub.prox.tagservice.tag.recommendation.TagRecommendationCalculator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

public class TagRepositoryCustomImpl implements TagRepositoryCustom {

  private final TagRecommendationCalculator tagRecommendationCalculator;

  @Autowired
  public TagRepositoryCustomImpl(TagRecommendationCalculator tagRecommendationCalculator) {
    this.tagRecommendationCalculator = tagRecommendationCalculator;
  }

  @Override
  public List<Tag> tagRecommendations(@RequestParam("tagIds") UUID[] tagIds) {

    List<Tag> recommendedTags = tagRecommendationCalculator.getRecommendedTags(tagIds);

    return recommendedTags;
  }
}
