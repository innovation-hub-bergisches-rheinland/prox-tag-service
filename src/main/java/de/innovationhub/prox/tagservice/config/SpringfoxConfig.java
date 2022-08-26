package de.innovationhub.prox.tagservice.config;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringfoxConfig {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .forCodeGeneration(true)
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(jwtScheme()))
        .groupName("tags-service")
        .select()
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(List.of(defaultAuth()))
        .operationSelector(o -> o.httpMethod() != HttpMethod.GET)
        .build();
  }

  private SecurityReference defaultAuth() {
    return SecurityReference.builder().scopes(new AuthorizationScope[0]).reference("JWT").build();
  }

  private SecurityScheme jwtScheme() {
    return HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("JWT").build();
  }
}
