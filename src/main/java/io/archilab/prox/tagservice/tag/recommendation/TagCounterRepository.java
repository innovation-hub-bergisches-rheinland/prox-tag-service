package io.archilab.prox.tagservice.tag.recommendation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.UUID;
import io.archilab.prox.tagservice.tag.Tag;


@RepositoryRestResource(exported = false)
public interface TagCounterRepository extends CrudRepository<TagCounter, UUID> {
  List<TagCounter> findByTag1OrTag2(Tag tag1, Tag tag2);
}
