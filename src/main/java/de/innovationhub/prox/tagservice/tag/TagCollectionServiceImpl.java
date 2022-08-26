package de.innovationhub.prox.tagservice.tag;

import de.innovationhub.prox.tagservice.tag.dto.ReadTagPopularityDto;
import de.innovationhub.prox.tagservice.tag.dto.ReadTagRecommendationDto;
import de.innovationhub.prox.tagservice.tag.dto.ReadTagsDto;
import de.innovationhub.prox.tagservice.tag.dto.UpdateTagsDto;
import de.innovationhub.prox.tagservice.tag.exception.TagCollectionNotFoundException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.springframework.stereotype.Service;

@Service
public class TagCollectionServiceImpl implements TagCollectionService {
  private final TagCollectionRepository tagCollectionRepository;

  public TagCollectionServiceImpl(TagCollectionRepository tagCollectionRepository) {
    this.tagCollectionRepository = tagCollectionRepository;
  }

  @Transactional(TxType.REQUIRED)
  public void addTags(UUID id, UpdateTagsDto updateTagsDto) {
    // TODO: We need to create the collection while we don't have asynchronous messaging
    // TagCollection tagCollection = tagCollectionRepository.findById(id).orElseThrow(() -> new TagCollectionNotFoundException(id));
    var tagCollection = tagCollectionRepository.findById(id).orElse(new TagCollection(id));

    var tags = updateTagsDto.tags()
      .stream()
      .map(Tag::new)
      .collect(Collectors.toSet());

    tagCollection.setTags(tags);
    tagCollectionRepository.save(tagCollection);
  }

  @Override
  public ReadTagsDto getTags(UUID id) {
    TagCollection tagCollection = tagCollectionRepository.findById(id).orElseThrow(() -> new TagCollectionNotFoundException(id));
    return new ReadTagsDto(tagCollection.getTags().stream().map(Tag::getTag).collect(Collectors.toSet()));
  }

  @Override
  public ReadTagPopularityDto findPopularTags(Integer size) {
    var popularityMap = tagCollectionRepository.findPopularTags(size);
    return new ReadTagPopularityDto(popularityMap);
  }

  @Override
  public ReadTagRecommendationDto findRecommendedTags(Collection<String> tags) {
    var result = tagCollectionRepository.tagRecommendations(tags);
    return new ReadTagRecommendationDto(result.stream().map(Tag::getTag).collect(Collectors.toSet()));
  }
}
