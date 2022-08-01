package de.innovationhub.prox.tagservice.tag;


import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

public interface TagRepository extends PagingAndSortingRepository<Tag, UUID> {

  List<Tag> findByIdIn(Collection<UUID> ids);

  @RestResource(path = "findByTagName")
  Set<Tag> findByTagNameTagNameIgnoreCase(@Param(value = "tagName") String tagName);

  @RestResource(path = "findByTagNameContaining")
  Set<Tag> findByTagNameTagNameContainingIgnoreCase(@Param(value = "tagName") String tagName);

  @Query("""
          select t from TagCollection tc
          join tc.tags t
          where tc.referencedEntity IN (
            select tc2.referencedEntity from TagCollection tc2
            join tc2.tags t2
            where t2.id IN ?1
          )
          and t.id NOT IN ?1
          group by t.id
          order by count(t.id) desc
      """)
  List<Tag> tagRecommendations(@RequestParam("tagIds") Collection<UUID> tagIds, @PageableDefault(value = 15) Pageable pageable);
}
