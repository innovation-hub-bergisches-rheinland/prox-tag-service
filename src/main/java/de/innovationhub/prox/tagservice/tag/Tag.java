package de.innovationhub.prox.tagservice.tag;

import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Tag {
  private String tag;

  public Tag(String tag) {
    Objects.requireNonNull(tag);
    tag = tag.trim().toLowerCase();

    if(tag.isBlank()) {
      throw new IllegalArgumentException("Tag must not be blank");
    }

    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }

  protected void setTag(String tag) {
    this.tag = tag;
  }
}
