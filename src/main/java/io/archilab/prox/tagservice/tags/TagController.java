package io.archilab.prox.tagservice.tags;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestController
@ExposesResourceFor(Tag.class)
@RequestMapping("tags")
public class TagController {

  @Autowired
  private TagCounterRepository tagCounterRepository;

  @Autowired
  private TagRepository tagRepository;


  @RequestMapping(path = "", method = RequestMethod.POST, produces = { "application/hal+json" })
  public ResponseEntity<?> addTag(@RequestBody Tag tag) {
    tag = tagRepository.save(tag);
    return ResponseEntity.status(HttpStatus.CREATED).body(generateTagResource(tag));
  }

  @RequestMapping(path = "{id}", method = RequestMethod.PATCH, produces = { "application/hal+json" })
  public ResponseEntity<Resource<Tag>> patchTag(@PathVariable UUID id, @RequestBody Tag newTag) {
    final Optional<Tag> optTag = tagRepository.findById(id);
    if (!optTag.isPresent())
    {
      return ResponseEntity.notFound().build();
    }

    Tag tag = optTag.get();
    if (newTag.getTagName().getTagName() != null) {
      tag.setTagName(newTag.getTagName());
      tagRepository.save(tag);
    }

    return ResponseEntity.ok(generateTagResource(tag));
  }

  @RequestMapping(path = "{id}", method = RequestMethod.PUT, produces = { "application/hal+json" })
  public ResponseEntity<Resource<Tag>> putTag(@PathVariable UUID id, @RequestBody Tag newTag) {
    final Optional<Tag> optTag = tagRepository.findById(id);
    if (!optTag.isPresent())
    {
      return ResponseEntity.notFound().build();
    }

    Tag tag = optTag.get();
    tag.setTagName(newTag.getTagName());
    tagRepository.save(tag);

    return ResponseEntity.ok(generateTagResource(tag));
  }

  @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Resource<Tag>> deleteTag(@PathVariable UUID id) {
    final Optional<Tag> optTag = tagRepository.findById(id);
    if (!optTag.isPresent())
    {
      return ResponseEntity.notFound().build();
    }

    tagRepository.delete(optTag.get());

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "{id}", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<Resource<Tag>> getTag(@PathVariable UUID id) {
    final Optional<Tag> optTag = tagRepository.findById(id);
    if (!optTag.isPresent())
    {
      return ResponseEntity.notFound().build();
    }

    Tag tag = optTag.get();

    return ResponseEntity.ok(generateTagResource(tag));
  }

  @RequestMapping(path = "search", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<?> getSearchLinks() {
    Resources<?> resources = new Resources<>(new ArrayList<Resource<?>>());
    resources.add(ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(TagController.class).getTagByTagName(""))
        .withRel("findByTagName"));
    resources.add(ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(TagController.class).getSearchLinks())
        .withSelfRel());

    return ResponseEntity.ok(resources);
  }

  @RequestMapping(path = "search/findByTagName", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<Resource<Tag>> getTagByTagName(@RequestParam("tagName") String tagName) {
    final Optional<Tag> optTag = tagRepository.findByTagName_TagName(tagName);
    if (!optTag.isPresent())
    {
      return ResponseEntity.notFound().build();
    }

    Tag tag = optTag.get();

    return ResponseEntity.ok(generateTagResource(tag));
  }

  @RequestMapping(path = "", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<Resources<Resource<Tag>>> getTags() {
    Iterable<Tag> tags = tagRepository.findAll();

    Resources<Resource<Tag>> tagResources = generateTagResources(tags);

    tagResources.add(ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(TagController.class).getTags())
        .withSelfRel());
    tagResources.add(ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(TagController.class).getSearchLinks())
        .withRel("search"));

    return ResponseEntity.ok(tagResources);
  }

  @RequestMapping(path = "{id}/recommendations", method = RequestMethod.GET, produces = { "application/hal+json" })
  public ResponseEntity<Resources<Resource<Tag>>> tagRecommendations(@PathVariable("id") UUID id) {
    List<Tag> recommendedTags = getRecommendedTags(id);
    if (recommendedTags == null) {
      return ResponseEntity.notFound().build();
    }

    Resources<Resource<Tag>> recommendedTagResources = generateTagResources(recommendedTags);

    recommendedTagResources.add(ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(TagController.class).tagRecommendations(id))
        .withSelfRel());

    return ResponseEntity.ok(recommendedTagResources);
  }

  private Resource<Tag> generateTagResource(Tag tag) {
    Resource<Tag> tagResource = new Resource<>(tag);
    tagResource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TagController.class).getTag(tag.getId())).withSelfRel());
    tagResource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TagController.class).getTag(tag.getId())).withRel("tag"));
    tagResource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TagController.class).tagRecommendations(tag.getId())).withRel("recommendations"));
    return tagResource;
  }

  private Resources<Resource<Tag>> generateTagResources(Iterable<Tag> tags) {
    List<Resource<Tag>> tagResourceList = new ArrayList<>();
    for (Tag tag : tags) {
      tagResourceList.add(generateTagResource(tag));
    }

    return new Resources<Resource<Tag>>(tagResourceList);
  }

  private List<Tag> getRecommendedTags(UUID tagId) {
    Optional<Tag> optionalSearchTag = tagRepository.findById(tagId);
    if (!optionalSearchTag.isPresent()) {
      return null;
    }

    // Add Recommended Tags
    List<Tag> recommendedTags = new ArrayList<>();
    Tag searchTag = optionalSearchTag.get();
    for (TagCounter tagCounter : tagCounterRepository.findByTag1OrTag2OrderByCountDesc(searchTag,
        searchTag)) {
      recommendedTags.add(tagCounter.getOtherTag(searchTag));
    }

    // Add Remaining Tags
    for (Tag tag : tagRepository.findAll()) {
      if (!recommendedTags.contains(tag) && !tag.equals(searchTag)) {
        recommendedTags.add(tag);
      }
    }

    return recommendedTags;
  }
}
