package de.innovationhub.prox.tagservice.tag.recommendation;

import static org.junit.jupiter.api.Assertions.*;

import de.innovationhub.prox.tagservice.tag.Tag;
import de.innovationhub.prox.tagservice.tag.TagName;
import org.junit.jupiter.api.Test;

class TagCounterTest {

  @Test
  void testHashCode() {
    Tag tag1 = new Tag(new TagName("tag1"));
    Tag tag2 = new Tag(new TagName("tag2"));
    TagCounter tagCounter1 = new TagCounter(tag1, tag2, 2);
    TagCounter tagCounter2 = new TagCounter(tag1, tag2, 2);

    assertEquals(tagCounter1.hashCode(), tagCounter2.hashCode());
  }

  @Test
  void testEquals() {
    Tag tag1 = new Tag(new TagName("tag1"));
    Tag tag2 = new Tag(new TagName("tag2"));
    TagCounter tagCounter1 = new TagCounter(tag1, tag2, 2);
    TagCounter tagCounter2 = new TagCounter(tag1, tag2, 2);

    assertEquals(tagCounter1, tagCounter2);
  }

  @Test
  void testEqualsWithOtherObject() {
    Tag tag1 = new Tag(new TagName("tag1"));
    Tag tag2 = new Tag(new TagName("tag2"));
    TagCounter tagCounter1 = new TagCounter(tag1, tag2, 2);

    assertNotEquals(tagCounter1, new Object());
  }
}
