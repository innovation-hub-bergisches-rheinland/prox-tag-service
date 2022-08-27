package de.innovationhub.prox.tagservice.tag.events;


import de.innovationhub.prox.tagservice.tag.Tag;
import java.util.Set;
import java.util.UUID;

public record TagsAdded(UUID referencedEntity, Set<Tag> tags) {}
