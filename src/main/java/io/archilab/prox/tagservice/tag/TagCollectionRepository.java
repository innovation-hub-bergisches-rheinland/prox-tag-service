package io.archilab.prox.tagservice.tag;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagCollectionRepository extends PagingAndSortingRepository<TagCollection, UUID> {}
