package de.innovationhub.prox.tagservice.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest
public class TagValidationTest {
  @Autowired LocalValidatorFactoryBean localValidatorFactoryBean;

  @Test
  void when_project_name_is_null_should_return_violation() {
    Tag tag = new Tag();
    tag.setTagName(null);

    Set<ConstraintViolation<Tag>> violationSet = localValidatorFactoryBean.validate(tag);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_name_is_blank_should_return_violation() {
    Tag tag = new Tag();
    tag.setTagName(new TagName("  "));

    Set<ConstraintViolation<Tag>> violationSet = localValidatorFactoryBean.validate(tag);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_name_has_zwsp_should_return_violation() {
    Tag tag = new Tag();
    tag.setTagName(new TagName("a\u200Bb"));

    Set<ConstraintViolation<Tag>> violationSet = localValidatorFactoryBean.validate(tag);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_name_is_valid_should_not_return_violation() {
    Tag tag = new Tag();
    tag.setTagName(new TagName("Tag 1"));

    Set<ConstraintViolation<Tag>> violationSet = localValidatorFactoryBean.validate(tag);
    assertTrue(violationSet.isEmpty());
  }
}
