package de.innovationhub.prox.tagservice.tag.events.dto;


import java.util.Set;
import java.util.UUID;

public record ItemTaggedDto(UUID id, Set<String> tags) {}
