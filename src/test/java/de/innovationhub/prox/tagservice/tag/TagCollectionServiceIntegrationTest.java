package de.innovationhub.prox.tagservice.tag;

import static org.assertj.core.api.Assertions.assertThat;

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

  static final String ADDED_TOPIC = "event.item.tagged";
  static RedpandaContainer REDPANDA_CONTAINER =
      new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.2");

  static {
    REDPANDA_CONTAINER.start();
  }

  @DynamicPropertySource
  static void setupKafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", REDPANDA_CONTAINER::getBootstrapServers);
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
  }

  // https://github.com/testcontainers/testcontainers-java/blob/master/modules/redpanda/src/main/java/org/testcontainers/redpanda/RedpandaContainer.java
  static class RedpandaContainer extends GenericContainer<RedpandaContainer> {

    private static final String REDPANDA_FULL_IMAGE_NAME =
        "docker.redpanda.com/vectorized/redpanda";

    private static final DockerImageName REDPANDA_IMAGE =
        DockerImageName.parse(REDPANDA_FULL_IMAGE_NAME);

    private static final int REDPANDA_PORT = 9092;
    private static final int SCHEMA_REGISTRY_PORT = 8081;

    private static final String STARTER_SCRIPT = "/testcontainers_start.sh";

    public RedpandaContainer(String image) {
      this(DockerImageName.parse(image));
    }

    public RedpandaContainer(DockerImageName imageName) {
      super(imageName);
      imageName.assertCompatibleWith(REDPANDA_IMAGE);

      boolean isLessThanBaseVersion =
          new ComparableVersion(imageName.getVersionPart()).isLessThan("v22.2.1");
      if (REDPANDA_FULL_IMAGE_NAME.equals(imageName.getUnversionedPart())
          && isLessThanBaseVersion) {
        throw new IllegalArgumentException("Redpanda version must be >= v22.2.1");
      }

      withExposedPorts(REDPANDA_PORT, SCHEMA_REGISTRY_PORT);
      withCreateContainerCmdModifier(
          cmd -> {
            cmd.withEntrypoint("sh");
          });
      waitingFor(Wait.forLogMessage(".*Started Kafka API server.*", 1));
      withCommand(
          "-c", "while [ ! -f " + STARTER_SCRIPT + " ]; do sleep 0.1; done; " + STARTER_SCRIPT);
    }

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo) {
      super.containerIsStarting(containerInfo);

      String command = "#!/bin/bash\n";

      command += "/usr/bin/rpk redpanda start --mode dev-container ";
      command += "--kafka-addr PLAINTEXT://0.0.0.0:29092,OUTSIDE://0.0.0.0:9092 ";
      command +=
          "--advertise-kafka-addr PLAINTEXT://0.0.0.0:29092,OUTSIDE://"
              + getHost()
              + ":"
              + getMappedPort(9092);

      String config = "";

      copyFileToContainer(
          Transferable.of(command.getBytes(StandardCharsets.UTF_8), 0777), STARTER_SCRIPT);
    }

    public String getBootstrapServers() {
      return String.format("PLAINTEXT://%s:%s", getHost(), getMappedPort(REDPANDA_PORT));
    }

    public String getSchemaRegistryUrl() {
      return String.format("http://%s:%s", getHost(), getMappedPort(SCHEMA_REGISTRY_PORT));
    }
  }

  BlockingQueue<ConsumerRecord<String, ItemTaggedDto>> addedQueue = new LinkedBlockingQueue<>();

  @KafkaListener(topics = ADDED_TOPIC)
  void tagAddedListener(ConsumerRecord<String, ItemTaggedDto> record) {
    this.addedQueue.add(record);
  }

  @Test
  void shouldPublishOnTagAdded() throws InterruptedException {
    var id = UUID.randomUUID();
    tagCollectionService.addTags(id, new UpdateTagsDto(Set.of("a", "b", "c")));

    var record = this.addedQueue.poll(5, TimeUnit.SECONDS);
    assertThat(record)
        .isNotNull()
        .satisfies(
            r -> {
              assertThat(r.key()).isEqualTo(id.toString());
              assertThat(r.value().tags()).containsExactlyInAnyOrder("a", "b", "c");
            });
  }
}
