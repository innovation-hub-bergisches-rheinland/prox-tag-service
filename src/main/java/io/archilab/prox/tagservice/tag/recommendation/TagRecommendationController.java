package io.archilab.prox.tagservice.tag.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import io.archilab.prox.tagservice.tag.Tag;
import io.archilab.prox.tagservice.tag.TagController;
import io.archilab.prox.tagservice.tag.TagRepository;


@RepositoryRestController
@RequestMapping("tagRecommendations")
public class TagRecommendationController {

  @Autowired
  private TagCounterRepository tagCounterRepository;

  @Autowired
  private TagRepository tagRepository;

  private final int resultCount;


  @Autowired
  public TagRecommendationController(@Value("${tagRecommendationCalculation.resultCount}") int resultCount) {
    this.resultCount = resultCount;
  }

  @RequestMapping(path = "", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<Resources<Resource<Tag>>> tagRecommendations(@RequestParam("tags") UUID[] tagIds) {
    List<Tag> recommendedTags = getRecommendedTags(tagIds);

    Resources<Resource<Tag>> recommendedTagResources = generateTagResources(recommendedTags);

    recommendedTagResources.add(ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(TagRecommendationController.class).tagRecommendations(tagIds))
        .withSelfRel());

    return ResponseEntity.ok(recommendedTagResources);
  }

  private Resource<Tag> generateTagResource(Tag tag) {
    Resource<Tag> tagResource = new Resource<>(tag);
    tagResource.add(ControllerLinkBuilder.linkTo(TagController.class).slash(tag.getId()).withSelfRel());
    tagResource.add(ControllerLinkBuilder.linkTo(TagController.class).slash(tag.getId()).withRel("tag"));
    return tagResource;
  }

  private Resources<Resource<Tag>> generateTagResources(Iterable<Tag> tags) {
    List<Resource<Tag>> tagResourceList = new ArrayList<>();
    for (Tag tag : tags) {
      tagResourceList.add(generateTagResource(tag));
    }

    return new Resources<Resource<Tag>>(tagResourceList);
  }

  private List<Tag> getRecommendedTags(UUID[] tagIds) {
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
    sortedRecommendedTags.sort(new Comparator<Entry<Tag, Integer>>() {
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
