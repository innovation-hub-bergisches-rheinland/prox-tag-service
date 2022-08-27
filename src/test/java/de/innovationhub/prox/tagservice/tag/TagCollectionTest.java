package de.innovationhub.prox.tagservice.tag;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TagCollectionTest {
  @Test
  void shouldThrowWhenReferencedEntityIsNull() {
    assertThrows(NullPointerException.class, () -> new TagCollection(null));
  }

  @Test
  void shouldThrowWhenTagIsNull() {
    var tagCollection = new TagCollection(UUID.randomUUID());
    assertThrows(NullPointerException.class, () -> tagCollection.setTags(null));
  }

  @Test
  void shouldReturnImmutableTags() {
    var tagCollection = new TagCollection(UUID.randomUUID());
    var immutableTags = tagCollection.getTags();

    assertThrows(UnsupportedOperationException.class, () -> immutableTags.add(new Tag("tags")));
  }
}
