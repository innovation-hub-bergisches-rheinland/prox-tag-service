package io.archilab.prox.tagservice.tag.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import java.util.List;
import io.archilab.prox.tagservice.tag.Tag;
import io.archilab.prox.tagservice.tag.TagCollection;


@Component
@RepositoryEventHandler
public class TagCollectionEventHandler {

  @Autowired
  private TagCounterUpdater tagCounterUpdater;


  @HandleBeforeLinkSave
  public void handleTagCollectionUpdate(TagCollection tagCollection, List<Tag> oldTags) {
    tagCounterUpdater.updateTagCounter(tagCollection, oldTags);
  }
}
