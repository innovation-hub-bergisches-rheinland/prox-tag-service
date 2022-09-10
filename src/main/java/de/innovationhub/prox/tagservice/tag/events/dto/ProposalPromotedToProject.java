package de.innovationhub.prox.tagservice.tag.events.dto;

import java.util.UUID;

public record ProposalPromotedToProject(
  UUID proposalId,
  UUID projectId
) {

}
