package de.innovationhub.prox.tagservice.tag.recommendation;

import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagCollection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class TagCollectionEventHandler {

  @Autowired private TagCounterUpdater tagCounterUpdater;

  @HandleBeforeLinkSave
  public void handleTagCollectionUpdate(TagCollection tagCollection, List<Tag> oldTags) {
    tagCounterUpdater.updateTagCounter(tagCollection, oldTags);
  }
}
