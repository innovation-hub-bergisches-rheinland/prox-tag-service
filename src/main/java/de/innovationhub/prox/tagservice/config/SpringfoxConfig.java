package de.innovationhub.prox.tagservice.config;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.netflix.discovery.EurekaClient;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Server;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringfoxConfig {

  private final EurekaClient eurekaClient;

  public SpringfoxConfig(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .forCodeGeneration(true)
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(jwtScheme()))
        .groupName("prox-tag-service")
        .select()
        .paths(PathSelectors.ant("/tags/**")
            .or(PathSelectors.ant("/tagCollections/**")))
        .apis(customRequestHandlers())
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(List.of(defaultAuth()))
        .operationSelector(o -> o.httpMethod() != HttpMethod.GET)
        .build();
  }

  private SecurityReference defaultAuth() {
    return SecurityReference.builder()
        .scopes(new AuthorizationScope[0])
        .reference("JWT")
        .build();
  }

  private SecurityScheme jwtScheme() {
    return HttpAuthenticationScheme.JWT_BEARER_BUILDER
        .name("JWT")
        .build();
  }

  //TODO Remove when Controllers are implemented and use Swagger/Springfox annotations instead
  private java.util.function.Predicate<RequestHandler> customRequestHandlers() {
    return new Predicate<>() {
      @Override
      public boolean apply(@Nullable RequestHandler input) {
        if(input != null && input.getName() != null && input.getName().equals("findAllTagCollection")) {
          Set<RequestMethod> methodSet = input.supportedMethods();
          return methodSet.contains(RequestMethod.GET)
              || methodSet.contains(RequestMethod.OPTIONS)
              || methodSet.contains(RequestMethod.HEAD);
        } else if(input != null && input.getName() != null && input.getName().equals("tagCollectionTags")) {
          Set<RequestMethod> methodSet = input.supportedMethods();
          return methodSet.contains(RequestMethod.GET) || methodSet.contains(RequestMethod.PUT) || methodSet.contains(RequestMethod.POST); //NOTE PATCH is still displayed even if not supported
        } else if(input != null && input.getName() != null) {
          return !input.getName().equals("saveTagCollection") && !input.getName()
              .equals("tagCollectionTags") && !input.getName().equals("deleteTagCollection");
        }
        return true;
      }
    };
  }
}
