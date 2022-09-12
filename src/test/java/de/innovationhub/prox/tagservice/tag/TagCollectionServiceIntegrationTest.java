package de.innovationhub.prox.tagservice.tag;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.command.InspectContainerResponse;
import de.innovationhub.prox.tagservice.tag.dto.UpdateTagsDto;
import de.innovationhub.prox.tagservice.tag.events.dto.ItemTaggedDto;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.ComparableVersion;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("h2")
class TagCollectionServiceIntegrationTest {

  @Autowired private TagCollectionService tagCollectionService;
  @Autowired private ObjectMapper objectMapper;

  static final String ADDED_TOPIC = "event.item.tagged";
  static RedPandaContainer REDPANDA_CONTAINER =
      new RedPandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.2");

  static {
    REDPANDA_CONTAINER.start();
  }

  @DynamicPropertySource
  static void setupKafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", REDPANDA_CONTAINER::getBootstrapServers);
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
  }

  BlockingQueue<ConsumerRecord<String, String>> addedQueue = new LinkedBlockingQueue<>();

  @KafkaListener(topics = ADDED_TOPIC)
  void tagAddedListener(ConsumerRecord<String, String> record) throws Exception{
    this.addedQueue.add(record);
  }

  @Test
  void shouldPublishOnTagAdded() throws Exception {
    var id = UUID.randomUUID();
    tagCollectionService.addTags(id, new UpdateTagsDto(Set.of("a", "b", "c")));

    var record = this.addedQueue.poll(15, TimeUnit.SECONDS);
    var event = objectMapper.readValue(record.value(), ItemTaggedDto.class);
    assertThat(record)
        .isNotNull()
        .satisfies(
            r -> {
              assertThat(r.key()).isEqualTo(id.toString());
              assertThat(event.tags()).containsExactlyInAnyOrder("a", "b", "c");
            });
  }
}
