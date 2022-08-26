package de.innovationhub.prox.tagservice.tag;


import de.innovationhub.prox.tagservice.tag.data.TagData;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RepositoryEventHandler
@Component
public class TagEventHandler {
  private final KafkaTemplate<String, TagData> kafkaTemplate;
  private static final String TAG_TOPIC = "entity.tag.tags";

  public TagEventHandler(KafkaTemplate<String, TagData> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @HandleAfterCreate
  @HandleAfterSave
  protected void onAfterCreate(Tag tag) {
    var td = TagData.newBuilder()
      .setId(tag.getId().toString())
      .setTag(tag.getTagName().getTagName())
      .build();
    kafkaTemplate.send(TAG_TOPIC, tag.getId().toString(), td);
  }

  @HandleAfterDelete
  protected void onAfterDelete(Tag tag) {
    kafkaTemplate.send(TAG_TOPIC, tag.getId().toString(), null);
  }
}
