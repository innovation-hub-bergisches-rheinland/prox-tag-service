package de.innovationhub.prox.tagservice.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureDataJpa
@Transactional
class TagRepositoryTest {

  @Autowired
  TagRepository tagRepository;

  @Test
  void when_find_tag_by_tag_name_ignore_case_should_ignore_case() {
    Tag tag = new Tag(new TagName("test"));
    tagRepository.save(tag);

    Set<Tag> foundTags = tagRepository.findByTagNameTagNameIgnoreCase("TEST");

    assertFalse(foundTags.isEmpty());
    assertTrue(foundTags.contains(tag));
  }

  @Test
  void when_find_tag_by_tag_name_containing_ignore_case_should_ignore_case() {
    Tag tag = new Tag(new TagName("test"));
    tagRepository.save(tag);

    Set<Tag> foundTags = tagRepository.findByTagNameTagNameContainingIgnoreCase("ES");

    assertFalse(foundTags.isEmpty());
    assertTrue(foundTags.contains(tag));
  }

}
