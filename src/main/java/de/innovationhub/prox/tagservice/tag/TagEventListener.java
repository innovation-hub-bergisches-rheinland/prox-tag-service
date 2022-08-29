package de.innovationhub.prox.tagservice.tag;


import de.innovationhub.prox.tagservice.tag.events.TagsAdded;
import de.innovationhub.prox.tagservice.tag.events.dto.TagsAddedDto;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class TagEventListener {
  private static final String ADDED_TOPIC = "event.tags.added";

  private final KafkaTemplate<String, TagsAddedDto> kafkaTemplate;

  public TagEventListener(KafkaTemplate<String, TagsAddedDto> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @TransactionalEventListener(
      phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
  public void onTagsAdded(TagsAdded event) {
    final var dto = new TagsAddedDto(event.referencedEntity(), event.tags().stream().map(Tag::getTag).collect(
      Collectors.toSet()));
    final var record =
        new ProducerRecord<>(
            ADDED_TOPIC, event.referencedEntity().toString(), dto);
    var future = kafkaTemplate.send(record);

    try {
      var result = future.get(30, TimeUnit.SECONDS);
      log.debug("Produced event to topic '{}'", result.getRecordMetadata().topic());
    } catch (InterruptedException e) {
      log.error("Error while sending message to kafka. Thread has been interrupted", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException | TimeoutException e) {
      log.error("Error while sending message to kafka", e);
    }
  }
}
