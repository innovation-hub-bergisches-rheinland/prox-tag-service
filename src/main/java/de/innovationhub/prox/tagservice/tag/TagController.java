package de.innovationhub.prox.tagservice.tag;

import de.innovationhub.prox.tagservice.tag.dto.ReadTagPopularityDto;
import de.innovationhub.prox.tagservice.tag.dto.ReadTagRecommendationDto;
import de.innovationhub.prox.tagservice.tag.dto.ReadTagsDto;
import de.innovationhub.prox.tagservice.tag.dto.UpdateTagsDto;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TagController {
  private final TagCollectionService tagCollectionService;

  public TagController(TagCollectionService tagCollectionService) {
    this.tagCollectionService = tagCollectionService;
  }

  @PutMapping(value = "/tags/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<ReadTagsDto> addTags(@PathVariable UUID id, @RequestBody UpdateTagsDto updateTagsDto) {
    tagCollectionService.addTags(id, updateTagsDto);

    var newTags = tagCollectionService.getTags(id);

    return ResponseEntity.ok(newTags);
  }

  @GetMapping(value = "/tags/{id}", produces = "application/json")
  public ResponseEntity<ReadTagsDto> getTags(@PathVariable UUID id) {
    return ResponseEntity.ok(tagCollectionService.getTags(id));
  }

  @GetMapping(value = "/tags/search", produces = "application/json")
  public ResponseEntity<ReadTagsDto> getPopularTags(@RequestParam(value = "q") String query) {
    if(query == null || query.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query is required");
    }
    return ResponseEntity.ok(tagCollectionService.searchTags(query));
  }

  @GetMapping(value = "/tags/popular", produces = "application/json")
  public ResponseEntity<ReadTagPopularityDto> getPopularTags(@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
    return ResponseEntity.ok(tagCollectionService.findPopularTags(size));
  }

  @GetMapping(value = "/tags/recommendations", produces = "application/json")
  public ResponseEntity<ReadTagRecommendationDto> getPopularTags(@RequestParam("tags") Set<String> tags) {
    if(tags.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Tags are required"
      );
    }
    return ResponseEntity.ok(tagCollectionService.findRecommendedTags(tags));
  }
}
