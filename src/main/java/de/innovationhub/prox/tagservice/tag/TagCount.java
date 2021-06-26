package de.innovationhub.prox.tagservice.tag;

import lombok.Value;

@Value
public class TagCount {
  Tag tag;
  long count;
}
