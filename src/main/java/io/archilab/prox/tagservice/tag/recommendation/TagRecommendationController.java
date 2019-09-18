package io.archilab.prox.tagservice.tag.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.archilab.prox.tagservice.tag.Tag;
import io.archilab.prox.tagservice.tag.TagController;


@Component
@RepositoryRestController
@RequestMapping("tagRecommendations")
public class TagRecommendationController {

  @Autowired
  private TagRecommendationCalculator tagRecommendationCalculator;


  @RequestMapping(path = "", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<Resources<Resource<Tag>>> tagRecommendations(@RequestParam("tags") UUID[] tagIds) {
    List<Tag> recommendedTags = tagRecommendationCalculator.getRecommendedTags(tagIds);

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
}
