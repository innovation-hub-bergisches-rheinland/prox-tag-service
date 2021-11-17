package de.innovationhub.prox.tagservice.tag.recommendation;

import de.innovationhub.prox.tagservice.tag.Tag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface TagCounterRepository extends CrudRepository<TagCounter, UUID> {
  List<TagCounter> findByTag1OrTag2(Tag tag1, Tag tag2);

  Optional<TagCounter> findByTag1AndTag2(Tag tag1, Tag tag2);
}
