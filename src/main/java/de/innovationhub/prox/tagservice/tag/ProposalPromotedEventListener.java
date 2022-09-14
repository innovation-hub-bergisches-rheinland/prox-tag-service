package de.innovationhub.prox.tagservice.tag;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.tagservice.tag.dto.UpdateTagsDto;
import de.innovationhub.prox.tagservice.tag.events.dto.ProposalPromotedToProject;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * When a proposal is being promoted to a project, the ID changes - we need to update it.
 */
@Component
@Slf4j
public class ProposalPromotedEventListener {
  private static final String PROPOSAL_PROMOTED_TO_PROJECT = "event.proposal.promoted-to-project";

  private final TagCollectionService tagCollectionService;
  private final TagCollectionRepository tagCollectionRepository;
  private final ObjectMapper objectMapper;

  public ProposalPromotedEventListener(TagCollectionService tagCollectionService,
    TagCollectionRepository tagCollectionRepository, ObjectMapper objectMapper) {
    this.tagCollectionService = tagCollectionService;
    this.tagCollectionRepository = tagCollectionRepository;
    this.objectMapper = objectMapper;
  }


  @KafkaListener(topics = PROPOSAL_PROMOTED_TO_PROJECT)
  @Transactional
  public void onProposalPromoted(ConsumerRecord<String, String> record) {
    var value = record.value();

    ProposalPromotedToProject event;
    try {
      event = objectMapper.readValue(value, ProposalPromotedToProject.class);
    } catch (Exception e) {
      log.error("Could not parse event", e);
      return;
    }

    var optionalTagCollection = tagCollectionRepository.findById(event.proposalId());
    if(optionalTagCollection.isEmpty()) {
      return;
    }

    var tc = optionalTagCollection.get();

    this.tagCollectionService.addTags(event.projectId(), new UpdateTagsDto(tc.getTags().stream().map(Tag::getTag).collect(
      Collectors.toSet())));

    // We don't delete the old tag collection as the proposal might be restored at a later point.
    // TODO: We might need to mark the old collection as "stale"/"inactive" or something like this
    //  to have a better recommendation system
  }
}
