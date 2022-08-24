package de.innovationhub.prox.tagservice.tagcollection;


import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.data.TagCollectionData;
import de.innovationhub.prox.tagservice.tag.data.TagData;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RepositoryEventHandler
@Component
public class TagCollectionEventHandler {
  private final KafkaTemplate<String, TagCollectionData> kafkaTemplate;
  private static final String TAG_COLLECTION_TOPIC = "entity.tag.collections";

  public TagCollectionEventHandler(KafkaTemplate<String, TagCollectionData> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @HandleAfterSave
  @HandleAfterCreate
  public void handleTagCollectionSave(TagCollection tagCollection) {
    var td = TagCollectionData.newBuilder()
      .setId(tagCollection.getReferencedEntity().toString())
      .addAllTags(convertTags(tagCollection.getTags()))
      .build();
    kafkaTemplate.send(TAG_COLLECTION_TOPIC, tagCollection.getReferencedEntity().toString(), td);
  }

  @HandleAfterLinkSave
  public void handleTagCollectionLinkSave(TagCollection tagCollection, Set<Tag> tags) {
    var td = TagCollectionData.newBuilder()
      .setId(tagCollection.getReferencedEntity().toString())
      .addAllTags(convertTags(tagCollection.getTags()))
      .build();
    kafkaTemplate.send(TAG_COLLECTION_TOPIC, tagCollection.getReferencedEntity().toString(), td);
  }

  @HandleAfterDelete
  public void handleTagCollectionDelete(TagCollection tagCollection) {
    kafkaTemplate.send(TAG_COLLECTION_TOPIC, tagCollection.getReferencedEntity().toString(), null);
  }

  private Iterable<TagData> convertTags(Collection<Tag> tags) {
    return tags.stream()
      .map(tag -> TagData.newBuilder().setId(tag.getId().toString()).setTag(tag.getTagName().getTagName()).build())
      .toList();
  }
}
