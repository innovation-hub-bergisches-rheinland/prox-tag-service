package de.innovationhub.prox.tagservice.tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@ComponentScan
class TagTest {

  @Autowired TagRepository tagRepository;

  @Test
  void tagNameConstraints() {

    String name_5 = "12345";
    String name_40 = "1234567890123456789012345678901234567890";
    String name_100 =
        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

    TagName tagName_5_a = new TagName(name_5);
    TagName tagName_5_b = new TagName(name_5);

    assertEquals(tagName_5_a, tagName_5_b);
    assertEquals(tagName_5_a.getTagName(), name_5);

    TagName tagName_40 = new TagName(name_40);

    assertEquals(tagName_40.getTagName(), name_40);

    try {
      TagName tagName_100 = new TagName(name_100);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), containsString("exceeded maximum number of"));
    }
  }

  @Test
  void tagUniqueName() {
    Tag tag_1 = new Tag(new TagName("Tag_1"));
    Tag tag_2 = new Tag(new TagName("Tag_2"));
    Tag tag_fail = new Tag(new TagName("Tag_1"));

    this.tagRepository.save(tag_1);
    assertEquals(1, this.tagRepository.count());

    this.tagRepository.save(tag_2);
    assertEquals(2, this.tagRepository.count());

    try {
      this.tagRepository.save(tag_fail);
      assertEquals(2, this.tagRepository.count());
    } catch (DataIntegrityViolationException e) {
      return;
    }

    fail("Tag name is not unique not detected!");
  }

  @Test
  void tagCreate() {
    String name_5 = "12345";
    String name_40 = "12345678901234567890";

    TagName tagName_5 = new TagName(name_5);
    TagName tagName_40 = new TagName(name_40);

    Tag tag_5 = new Tag(tagName_5);
    Tag tag_40 = new Tag(tagName_40);

    assertEquals(tag_5.getTagName().getTagName(), name_5);
    assertEquals(tag_40.getTagName().getTagName(), name_40);
  }

  @Test
  void tagCompare() {
    Tag tag1 = new Tag(new TagName("Tag 1"));
    Tag tag2 = new Tag(new TagName("Tag 2"));
    Tag tag3 = new Tag(new TagName("Tag 1"));

    assertTrue(tag1.compareTo(tag2) < 0);
    assertTrue(tag2.compareTo(tag1) > 0);
    assertEquals(0, tag3.compareTo(tag1));
  }
}
