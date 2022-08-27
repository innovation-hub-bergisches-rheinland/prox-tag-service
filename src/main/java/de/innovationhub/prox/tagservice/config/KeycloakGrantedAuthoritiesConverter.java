package de.innovationhub.prox.tagservice.config;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakGrantedAuthoritiesConverter
    implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(Jwt source) {
    Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");

    var roles = (List<String>) realmAccess.get("roles");
    Stream<GrantedAuthority> roleAuthorities = Stream.empty();
    if (roles != null) {
      roleAuthorities = roles.stream().map(rn -> new SimpleGrantedAuthority("ROLE_" + rn));
    }

    var features = source.getClaimAsStringList("features");
    Stream<GrantedAuthority> featureAuthorities = Stream.empty();
    if (features != null) {
      featureAuthorities = features.stream().map(fn -> new SimpleGrantedAuthority("FEATURE_" + fn));
    }

    return Stream.concat(roleAuthorities, featureAuthorities).collect(Collectors.toList());
  }
}
