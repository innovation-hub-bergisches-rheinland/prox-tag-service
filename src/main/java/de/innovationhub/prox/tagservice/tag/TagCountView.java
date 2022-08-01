package de.innovationhub.prox.tagservice.tag;

import java.util.UUID;
import lombok.Getter;

@Getter
public class TagCountView {
  private final UUID id;
  private final String tagName;
  private final long cnt;

  public TagCountView(UUID id, String tagName, long cnt) {
    this.id = id;
    this.tagName = tagName;
    this.cnt = cnt;
  }
}
