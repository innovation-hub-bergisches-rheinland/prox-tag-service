package io.archilab.prox.tagservice.tag.recommendation;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import io.archilab.prox.tagservice.core.AbstractEntity;
import io.archilab.prox.tagservice.tag.Tag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagCounter extends AbstractEntity {

  @ManyToOne
  private Tag tag1;
  @ManyToOne
  private Tag tag2;

  @Setter
  private int count;


  public TagCounter(Tag tag1, Tag tag2, int count) {
    if (tag1.getId().toString().compareTo(tag2.getId().toString()) < 0) {
      this.tag1 = tag1;
      this.tag2 = tag2;
    } else {
      this.tag1 = tag2;
      this.tag2 = tag1;
    }

    this.count = count;
  }

  @Override
  public int hashCode() {
    return (tag1.getId().toString() + tag2.getId().toString()).hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TagCounter)) {
      return false;
    }
    return (tag1.equals(((TagCounter)(o)).getTag1())
        && tag2.equals(((TagCounter)(o)).getTag2()));
  }

  public Tag getOtherTag(Tag tag) {
    return tag.equals(tag1) ? tag2 : tag1;
  }
}
