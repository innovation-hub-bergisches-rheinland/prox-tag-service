package io.archilab.prox.tagservice.tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import io.archilab.prox.tagservice.tag.recommendation.TagRecommendationCalculator;

public class TagRepositoryCustomImpl implements TagRepositoryCustom {

  @Autowired
  private TagRecommendationCalculator tagRecommendationCalculator;

  @Override
  public List<Tag> tagRecommendations(@RequestParam("tagIds") UUID[] tagIds) {

    List<Tag> recommendedTags = tagRecommendationCalculator.getRecommendedTags(tagIds);

    return recommendedTags;
  }


}
