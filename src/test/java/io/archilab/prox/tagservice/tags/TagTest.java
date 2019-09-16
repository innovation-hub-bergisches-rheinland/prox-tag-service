package io.archilab.prox.tagservice.tags;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TagTest {

  @Autowired
  TagRepository tagRepository;

  @Test
  public void tagNameConstraints() {

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
  public void tagUniqueName() {
    Tag tag_1 = new Tag(new TagName("Tag_1"));
    Tag tag_2 = new Tag(new TagName("Tag_2"));
    Tag tag_fail = new Tag(new TagName("Tag_1"));

    this.tagRepository.save(tag_1);
    Assert.assertEquals(1, this.tagRepository.count());

    this.tagRepository.save(tag_2);
    Assert.assertEquals(2, this.tagRepository.count());

    try {
      this.tagRepository.save(tag_fail);
      Assert.assertEquals(2, this.tagRepository.count());
    } catch (DataIntegrityViolationException e) {
      return;
    }

    fail("Tag name is not unique not detected!");
  }

  @Test
  public void tagCreate() {
    String name_5 = "12345";
    String name_40 = "12345678901234567890";

    TagName tagName_5 = new TagName(name_5);
    TagName tagName_40 = new TagName(name_40);

    Tag tag_5 = new Tag(tagName_5);
    Tag tag_40 = new Tag(tagName_40);

    assertEquals(tag_5.getTagName().getTagName(), name_5);
    assertEquals(tag_40.getTagName().getTagName(), name_40);
  }
}
