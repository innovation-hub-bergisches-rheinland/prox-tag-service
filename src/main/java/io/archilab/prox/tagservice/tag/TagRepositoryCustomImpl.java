package io.archilab.prox.tagservice.tag;

import io.archilab.prox.tagservice.tag.recommendation.TagRecommendationCalculator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

public class TagRepositoryCustomImpl implements TagRepositoryCustom {

  @Autowired private TagRecommendationCalculator tagRecommendationCalculator;

  @Override
  public List<Tag> tagRecommendations(@RequestParam("tagIds") UUID[] tagIds) {

    List<Tag> recommendedTags = tagRecommendationCalculator.getRecommendedTags(tagIds);

    return recommendedTags;
  }
}
