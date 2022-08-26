package de.innovationhub.prox.tagservice.tag;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TagTest {
  @Test
  void shouldThrowWhenTagIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> new Tag(""));
  }

  @Test
  void shouldThrowWhenTagIsNull() {
    assertThrows(NullPointerException.class, () -> new Tag(null));
  }

  @Test
  void shouldTrimTag() {
    var tag = new Tag("  tags  ");
    assertEquals("tags", tag.getTag());
  }

  @Test
  void shouldLowerCaseTag() {
    var tag = new Tag("TAG");
    assertEquals("tag", tag.getTag());
  }
}
