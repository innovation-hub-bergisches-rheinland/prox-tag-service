package de.innovationhub.prox.tagservice.tag;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.*;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@AllArgsConstructor
public class TagCollection {

  // Project
  @Id private UUID referencedEntity;

  @ManyToMany private Set<Tag> tags = new HashSet<>();

  public TagCollection(UUID referencedEntity) {
    this.referencedEntity = referencedEntity;
  }

  public void addTags(Iterable<Tag> tags) {
    tags.forEach(this::addTag);
  }

  public void addTag(Tag tag) {
    this.tags.add(tag);
  }
}
