package de.innovationhub.prox.tagservice.config;

import de.innovationhub.prox.tagservice.utils.AuthenticationUtils;
import de.innovationhub.prox.tagservice.utils.KeycloakAuthenticationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
  @Bean
  public AuthenticationUtils authenticationUtils() {
    return new KeycloakAuthenticationUtils();
  }
}
