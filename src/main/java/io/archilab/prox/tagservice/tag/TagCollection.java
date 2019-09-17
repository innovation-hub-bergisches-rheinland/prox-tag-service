package io.archilab.prox.tagservice.tag;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TagCollection {

  @Id
  private UUID referencedEntity;

  @ManyToMany
  private List<Tag> tagCollection;


  public TagCollection(UUID referencedEntity) {
    this.referencedEntity = referencedEntity;
  }
}
