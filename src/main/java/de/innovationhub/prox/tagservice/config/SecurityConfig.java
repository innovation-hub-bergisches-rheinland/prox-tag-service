package de.innovationhub.prox.tagservice.config;


import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
class SecurityConfig {

  public static final String[] SWAGGER_PATHS = {
    "/swagger-resources/**", "/swagger-ui/**", "/swagger-ui/", "/v2/api-docs", "/v3/api-docs"
  };

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter());
    jwtConverter.setPrincipalClaimName("sub");
    return jwtConverter;
  }

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setAllowedOrigins(List.of("*"));
    corsConfiguration.setAllowedMethods(List.of("*"));

    http.cors()
        .configurationSource(request -> corsConfiguration)
        .and()
        .csrf()
        .disable()
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()))
        .authorizeHttpRequests(
            registry ->
                registry
                    .mvcMatchers(HttpMethod.GET, SWAGGER_PATHS)
                    .permitAll()
                    .mvcMatchers(HttpMethod.HEAD, SWAGGER_PATHS)
                    .permitAll()
                    .mvcMatchers(HttpMethod.OPTIONS, SWAGGER_PATHS)
                    .permitAll()
                    .mvcMatchers(HttpMethod.GET, "/tags/**")
                    .permitAll()
                    .mvcMatchers(HttpMethod.HEAD, "/tags/**")
                    .permitAll()
                    .mvcMatchers(HttpMethod.OPTIONS, "/tags/**")
                    .permitAll()
                    .mvcMatchers("/tags/**")
                    // TODO
                    .authenticated()
                    .anyRequest()
                    .denyAll());

    return http.build();
  }
}
