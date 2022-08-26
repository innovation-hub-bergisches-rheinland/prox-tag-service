package de.innovationhub.prox.tagservice.tag;


import java.util.Map;

public interface TagCollectionRepositoryExtension {
  Map<String, Integer> findPopularTags(Integer size);
}
