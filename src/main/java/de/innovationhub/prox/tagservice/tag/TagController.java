package de.innovationhub.prox.tagservice.tag;


import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
@ExposesResourceFor(Tag.class)
public class TagController {

  private final TagCollectionRepository tagCollectionRepository;

  @Autowired
  public TagController(TagCollectionRepository tagCollectionRepository) {
    this.tagCollectionRepository = tagCollectionRepository;
  }

  @GetMapping("tags/search/popularTags")
  public @ResponseBody ResponseEntity<List<TagCount>> popularTags(
      @RequestParam(required = false, defaultValue = "10", name = "limit") Integer limit) {
    var popularTags =
        this.tagCollectionRepository.findAllUsedTags().stream()
            .flatMap(t -> t.stream())
            .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(Entry::getValue, Comparator.reverseOrder()))
            .limit(limit)
            .map(entry -> new TagCount(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

    return ResponseEntity.ok(popularTags);
  }
}
