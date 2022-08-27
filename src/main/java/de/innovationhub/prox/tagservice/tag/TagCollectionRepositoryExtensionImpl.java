package de.innovationhub.prox.tagservice.tag;


import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import org.springframework.stereotype.Component;

@Component
public class TagCollectionRepositoryExtensionImpl implements TagCollectionRepositoryExtension {

  private final EntityManager entityManager;

  public TagCollectionRepositoryExtensionImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Map<String, Integer> findPopularTags(Integer size) {
    var query =
        entityManager.createQuery(
            """
        select t.tag as tag, count(t) as cnt
          from TagCollection tc
          join tc.tags t
          group by t.tag
          order by cnt desc
        """,
            Tuple.class);
    query.setMaxResults(size);

    return query
        .getResultStream()
        .collect(
            Collectors.toMap(
                tuple -> (String) tuple.get("tag"),
                tuple -> ((Number) tuple.get("cnt")).intValue()));
  }
}
