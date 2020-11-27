package de.innovationhub.prox.tagservice.config;

import com.google.common.base.Predicate;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringfoxConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .forCodeGeneration(true)
        .select()
        .paths(PathSelectors.ant("/tags/**")
            .or(PathSelectors.ant("/tagCollections/**")))
        .apis(customRequestHandlers())
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
          return methodSet.contains(RequestMethod.PUT) || methodSet.contains(RequestMethod.POST); //NOTE PATCH is still displayed even if not supported
        } else if(input != null && input.getName() != null) {
          return !input.getName().equals("saveTagCollection") && !input.getName()
              .equals("tagCollectionTags") && !input.getName().equals("deleteTagCollection");
        }
        return true;
      }
    };
  }
}
