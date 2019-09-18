package io.archilab.prox.tagservice.tag;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;
import java.util.UUID;


@RepositoryRestController
@ExposesResourceFor(TagCollection.class)
@RequestMapping("tagCollections")
public class TagCollectionController {

  @Autowired
  private TagCollectionRepository tagCollectionRepository;


  @RequestMapping(path = "{id}", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<Resource<TagCollection>> getTagCollection(@PathVariable UUID id) {
    final Optional<TagCollection> optTagCollection = tagCollectionRepository.findById(id);
    TagCollection tagCollection;
    if (!optTagCollection.isPresent())
    {
      tagCollection = tagCollectionRepository.save(new TagCollection(id));
    } else {
      tagCollection = optTagCollection.get();
    }

    return ResponseEntity.ok(generateTagCollectionResource(tagCollection));
  }

  private Resource<TagCollection> generateTagCollectionResource(TagCollection tagCollection) {
    Resource<TagCollection> tagCollectionResource = new Resource<>(tagCollection);
    tagCollectionResource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TagCollectionController.class).getTagCollection(tagCollection.getReferencedEntity())).withSelfRel());
    tagCollectionResource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TagCollectionController.class).getTagCollection(tagCollection.getReferencedEntity())).withRel("tagCollection"));
    tagCollectionResource.add(ControllerLinkBuilder.linkTo(TagCollectionController.class).slash(tagCollection.getReferencedEntity()).slash("tags").withRel("tags"));
    return tagCollectionResource;
  }
}
