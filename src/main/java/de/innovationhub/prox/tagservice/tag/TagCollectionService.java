package de.innovationhub.prox.tagservice.tag;

import de.innovationhub.prox.tagservice.tag.dto.ReadTagPopularityDto;
import de.innovationhub.prox.tagservice.tag.dto.ReadTagRecommendationDto;
import de.innovationhub.prox.tagservice.tag.dto.ReadTagsDto;
import de.innovationhub.prox.tagservice.tag.dto.UpdateTagsDto;
import java.util.Collection;
import java.util.UUID;

public interface TagCollectionService {
  void addTags(UUID id, UpdateTagsDto updateTagsDto);
  ReadTagsDto getTags(UUID id);

  ReadTagPopularityDto findPopularTags(Integer size);

  ReadTagRecommendationDto findRecommendedTags(Collection<String> tags);
}
