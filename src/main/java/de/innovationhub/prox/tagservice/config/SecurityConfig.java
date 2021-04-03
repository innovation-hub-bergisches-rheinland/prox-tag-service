/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.tagservice.config;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  public static final String[] SWAGGER_PATHS = {
      "/swagger-resources/**",
      "/swagger-ui/**",
      "/swagger-ui/",
      "/v2/api-docs",
      "/v3/api-docs"

  };

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider =
        this.keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/tags/**")
        .permitAll()
        .antMatchers(HttpMethod.HEAD, "/tags/**")
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/tags/**")
        .permitAll()
        .antMatchers("/tags/**")
        .hasRole("professor")
        .antMatchers(HttpMethod.GET, "/tagCollections/**")
        .permitAll()
        .antMatchers(HttpMethod.HEAD, "/tagCollections/**")
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/tagCollections/**")
        .permitAll()
        .antMatchers(HttpMethod.DELETE, "/tagCollections/**")
        .denyAll()
        .antMatchers("/tagCollections/{id}/**")
        .access(
            "hasRole('professor') and @webSecurity.checkProjectCreator(request, #id, @projectClient)")
        .antMatchers(HttpMethod.POST, "/tagCollections/{id}/tags/**")
        .access(
            "hasRole('professor') and @webSecurity.checkProjectCreator(request, #id, @projectClient)")
        .antMatchers(HttpMethod.PUT, "/tagCollections/{id}/tags/**")
        .access(
            "hasRole('professor') and @webSecurity.checkProjectCreator(request, #id, @projectClient)")
        .antMatchers(HttpMethod.GET, "/tagRecommendations/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/tagCollections/**")
        .denyAll()
        .antMatchers(HttpMethod.GET, SWAGGER_PATHS)
        .permitAll()
        .anyRequest()
        .denyAll();
  }
}
