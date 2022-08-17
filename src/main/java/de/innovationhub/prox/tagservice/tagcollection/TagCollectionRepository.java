package de.innovationhub.prox.tagservice.tagcollection;


import de.innovationhub.prox.tagservice.tag.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagCollectionRepository extends PagingAndSortingRepository<TagCollection, UUID> {
  @Query("select tc.tags from TagCollection tc left join tc.tags")
  List<List<Tag>> findAllUsedTags();

  @Query("select tc from TagCollection tc left join tc.tags t where t.id in (?1)")
  List<TagCollection> findAllUsingTags(UUID[] tagIds);

  @Query("select tc from TagCollection tc left join tc.tags t where t.tagName.tagName in (?1)")
  List<TagCollection> findAllUsingTagsUsingName(String[] tags);
}
