package de.innovationhub.prox.tagservice.tag;


import java.util.ArrayList;
import java.util.List;
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

  @ManyToMany private List<Tag> tags = new ArrayList<>();

  public TagCollection(UUID referencedEntity) {
    this.referencedEntity = referencedEntity;
  }

  public void addTags(Iterable<Tag> tags) {
    tags.forEach(this::addTag);
  }

  public void addTag(Tag tag) {
    if (!this.tags.contains(tag)) this.tags.add(tag);
  }
}
