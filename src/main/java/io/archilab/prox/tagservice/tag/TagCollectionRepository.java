package io.archilab.prox.tagservice.tag;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface TagCollectionRepository extends PagingAndSortingRepository<TagCollection, UUID> {

}
