package io.archilab.prox.tagservice.tags;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;



@RepositoryRestResource(collectionResourceRel = "tags", path = "tags")
public interface TagRepository extends PagingAndSortingRepository<Tag, UUID> {

  Optional<Tag> findByTagName_TagName(@Param(value = "tagName") String tagName);

  Set<Tag> findByTagName_TagNameContaining(@Param(value = "tagName") String tagName);

}
