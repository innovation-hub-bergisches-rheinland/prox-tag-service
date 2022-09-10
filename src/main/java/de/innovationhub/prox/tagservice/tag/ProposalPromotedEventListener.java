package de.innovationhub.prox.tagservice.tag;


import de.innovationhub.prox.tagservice.tag.dto.UpdateTagsDto;
import de.innovationhub.prox.tagservice.tag.events.dto.ProposalPromotedToProject;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
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

  public ProposalPromotedEventListener(TagCollectionService tagCollectionService,
    TagCollectionRepository tagCollectionRepository) {
    this.tagCollectionService = tagCollectionService;
    this.tagCollectionRepository = tagCollectionRepository;
  }


  @KafkaListener(topics = PROPOSAL_PROMOTED_TO_PROJECT)
  public void onTagsAdded(@Payload ProposalPromotedToProject event) {
    var optionalTagCollection = tagCollectionRepository.findById(event.proposalId());
    if(optionalTagCollection.isEmpty()) {
      return;
    }

    var tc = optionalTagCollection.get();

    this.tagCollectionService.addTags(event.projectId(), new UpdateTagsDto(tc.getTags().stream().map(Tag::getTag).collect(
      Collectors.toSet())));

    tagCollectionRepository.delete(tc);
  }
}
