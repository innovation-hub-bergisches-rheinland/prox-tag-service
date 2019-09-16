package io.archilab.prox.tagservice.tags;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.UUID;


@RepositoryRestResource(exported = false)
public interface TagCounterRepository extends CrudRepository<TagCounter, UUID> {
  List<TagCounter> findByTag1OrTag2OrderByCountDesc(Tag tag1, Tag tag2);
}
