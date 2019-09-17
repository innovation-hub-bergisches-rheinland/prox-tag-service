package io.archilab.prox.tagservice.tag;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.archilab.prox.tagservice.core.AbstractEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"tagName"})})
public class Tag extends AbstractEntity {

  @Setter
  @JsonUnwrapped
  private TagName tagName;


  public Tag(TagName tagName) {
    this.tagName = tagName;
  }
}
