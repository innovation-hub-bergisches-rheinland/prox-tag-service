package de.innovationhub.prox.tagservice.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class TagControllerIntegrationTest {
  @Autowired MockMvc mockMvc;

  @Autowired EntityManager entityManager;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  void shouldReturn404() {
    var id = UUID.randomUUID();

    RestAssuredMockMvc.given()
        .accept("application/json")
        .when()
        .get("/tags/{id}", id)
        .then()
        .statusCode(404);
  }

  @Test
  void shouldReturnTags() {
    var sampleTags = sampleTags("a", "b", "c");
    var id = UUID.randomUUID();
    var tagCollection = new TagCollection(id);
    tagCollection.setTags(sampleTags);

    entityManager.persist(tagCollection);

    RestAssuredMockMvc.given()
        .accept("application/json")
        .when()
        .get("/tags/{id}", id)
        .then()
        .statusCode(200)
        .body("tags", hasSize(3))
        .body("tags", hasItems("a", "b", "c"));
  }

  @Test
  void shouldReturnPopularTags() {
    var tagCollection1 = new TagCollection(UUID.randomUUID());
    tagCollection1.setTags(sampleTags("a", "b", "c"));
    var tagCollection2 = new TagCollection(UUID.randomUUID());
    tagCollection2.setTags(sampleTags("a", "b"));

    entityManager.persist(tagCollection1);
    entityManager.persist(tagCollection2);

    Map<String, Integer> result =
        RestAssuredMockMvc.given()
            .queryParam("size", 3)
            .accept("application/json")
            .when()
            .get("/tags/popular")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getMap("popularity");

    assertThat(result).hasSize(3).containsOnly(entry("a", 2), entry("b", 2), entry("c", 1));
  }

  @Test
  void shouldSearchTags() {
    var tagCollection1 = new TagCollection(UUID.randomUUID());
    tagCollection1.setTags(sampleTags("abcdefg"));

    entityManager.persist(tagCollection1);

    RestAssuredMockMvc.given()
        .queryParam("q", "bcd")
        .accept("application/json")
        .when()
        .get("/tags/search")
        .then()
        .statusCode(200)
        .body("tags", hasSize(1))
        .body("tags", hasItems("abcdefg"));
  }

  @Test
  void shouldReturnRecommendationsTags() {
    var tagCollection1 = new TagCollection(UUID.randomUUID());
    tagCollection1.setTags(sampleTags("a", "b", "c"));
    var tagCollection2 = new TagCollection(UUID.randomUUID());
    tagCollection2.setTags(sampleTags("a", "b", "d"));

    entityManager.persist(tagCollection1);
    entityManager.persist(tagCollection2);

    RestAssuredMockMvc.given()
        .queryParam("tags", "a", "b")
        .accept("application/json")
        .when()
        .get("/tags/recommendations")
        .then()
        .statusCode(200)
        .body("recommendations", hasSize(2))
        .body("recommendations", hasItems("c", "d"));
  }

  @Test
  void shouldReturnUnauthorized() {
    RestAssuredMockMvc.given()
        .body("""
        {
          "tags": ["a", "b", "c"]
        }
      """)
        .accept("application/json")
        .contentType("application/json")
        .when()
        .put("/tags/{id}", UUID.randomUUID())
        .then()
        .statusCode(401);
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldSetTags() {
    var id = UUID.randomUUID();
    var tagCollection = new TagCollection(id);
    entityManager.persist(tagCollection);

    RestAssuredMockMvc.given()
        .body("""
        {
          "tags": ["a", "b", "c"]
        }
      """)
        .accept("application/json")
        .contentType("application/json")
        .when()
        .put("/tags/{id}", id)
        .then()
        .statusCode(200)
        .body("tags", hasSize(3))
        .body("tags", hasItems("a", "b", "c"));

    var found = entityManager.find(TagCollection.class, id);
    assertThat(found.getTags())
        .hasSize(3)
        .extracting(Tag::getTag)
        .containsExactlyInAnyOrder("a", "b", "c");
  }

  // TODO: Only needed while we have no real asynchronous processing
  @Test
  @WithMockUser(roles = "professor")
  void shouldCreateTagCollectionOnPutIfNotExistent() {
    var id = UUID.randomUUID();
    RestAssuredMockMvc.given()
        .body("""
        {
          "tags": ["a", "b", "c"]
        }
      """)
        .accept("application/json")
        .contentType("application/json")
        .when()
        .put("/tags/{id}", id)
        .then()
        .statusCode(200)
        .body("tags", hasSize(3))
        .body("tags", hasItems("a", "b", "c"));

    var found = entityManager.find(TagCollection.class, id);
    assertThat(found).isNotNull();
  }

  private List<Tag> sampleTags(String... tags) {
    return Arrays.stream(tags).map(Tag::new).toList();
  }
}
