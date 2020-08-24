package io.archilab.prox.tagservice.config;

import io.archilab.prox.tagservice.utils.AuthenticationUtils;
import io.archilab.prox.tagservice.utils.KeycloakAuthenticationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
  @Bean
  public AuthenticationUtils authenticationUtils() {
    return new KeycloakAuthenticationUtils();
  }
}