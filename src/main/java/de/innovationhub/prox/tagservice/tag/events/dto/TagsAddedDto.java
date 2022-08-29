package de.innovationhub.prox.tagservice.tag.events.dto;


import de.innovationhub.prox.tagservice.tag.Tag;
import java.util.Set;
import java.util.UUID;

public record TagsAddedDto(UUID id, Set<String> tags) {}
