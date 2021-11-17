package de.innovationhub.prox.tagservice.tag;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface TagRepository extends PagingAndSortingRepository<Tag, UUID>, TagRepositoryCustom {

  @RestResource(path = "findByTagName")
  Set<Tag> findByTagNameTagNameIgnoreCase(@Param(value = "tagName") String tagName);

  @RestResource(path = "findByTagNameContaining")
  Set<Tag> findByTagNameTagNameContainingIgnoreCase(@Param(value = "tagName") String tagName);
}
