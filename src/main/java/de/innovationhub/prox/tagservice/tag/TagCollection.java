package de.innovationhub.prox.tagservice.tag;


import de.innovationhub.prox.tagservice.tag.events.ItemTagged;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class TagCollection extends AbstractAggregateRoot<TagCollection> {

  // Some Entity ID from other services. Might be a project, proposal, profile, etc.
  @Id private UUID referencedEntity;

  @ElementCollection
  @Setter(AccessLevel.NONE)
  private Set<Tag> tags = new HashSet<>();

  public TagCollection(UUID referencedEntity) {
    Objects.requireNonNull(referencedEntity);

    this.referencedEntity = referencedEntity;
  }

  public void setTags(Collection<Tag> tags) {
    Objects.requireNonNull(tags);

    this.tags = new HashSet<>(tags);
    registerEvent(new ItemTagged(this.referencedEntity, this.tags));
  }

  public Set<Tag> getTags() {
    // Always return a immutable copy
    return Set.copyOf(tags);
  }
}
