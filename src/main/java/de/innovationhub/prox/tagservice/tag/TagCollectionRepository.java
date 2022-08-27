package de.innovationhub.prox.tagservice.tag;


import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagCollectionRepository
    extends PagingAndSortingRepository<TagCollection, UUID>, TagCollectionRepositoryExtension {
  @Query("select tc from TagCollection tc left join tc.tags t where t.tag in (?1)")
  List<TagCollection> findAllUsingTags(Collection<String> tags);

  @Query(
      """
          select t from TagCollection tc
          join tc.tags t
          where tc.referencedEntity IN (
            select tc2.referencedEntity from TagCollection tc2
            join tc2.tags t2
            where t2.tag IN ?1
          )
          and t.tag NOT IN ?1
          group by t.tag
          order by count(t.tag) desc
      """)
  List<Tag> tagRecommendations(Collection<String> tagNames);

  @Query(
      """
          select t from TagCollection tc
          join tc.tags t
          where t.tag like concat('%', lower(?1), '%')
      """)
  List<Tag> search(String query);
}
