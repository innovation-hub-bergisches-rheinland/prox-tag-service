package de.innovationhub.prox.tagservice.tag.dto;

import java.util.Set;

public record UpdateTagsDto(
  Set<String> tags
) {

}
