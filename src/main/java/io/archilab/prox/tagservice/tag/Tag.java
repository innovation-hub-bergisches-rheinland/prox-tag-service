package io.archilab.prox.tagservice.tag;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.prox.tagservice.core.AbstractEntity;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"tagName"})})
public class Tag extends AbstractEntity implements Comparable<Tag> {

  @Setter
  @JsonUnwrapped
  private TagName tagName;


  public Tag(TagName tagName) {
    this.tagName = tagName;
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }


  @Override
  public int compareTo(Tag o) {
    return this.getTagName().compareTo(o.getTagName());
  }
}
