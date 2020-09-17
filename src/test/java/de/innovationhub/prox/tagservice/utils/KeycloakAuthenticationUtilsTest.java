package de.innovationhub.prox.tagservice.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthenticationUtilsTest {

  private final KeycloakAuthenticationUtils keycloakAuthenticationUtils =
      new KeycloakAuthenticationUtils();

  @Mock private OidcKeycloakAccount account;

  @Mock private KeycloakAuthenticationToken keycloakAuthenticationToken;

  @Mock private KeycloakSecurityContext keycloakSecurityContext;

  @Mock private AccessToken accessToken;

  @Mock private HttpServletRequest request;

  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(keycloakAuthenticationToken.getAccount()).thenReturn(account);
    SecurityContextHolder.getContext().setAuthentication(keycloakAuthenticationToken);
    Mockito.when(account.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
    Mockito.when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
    Mockito.when(request.getUserPrincipal()).thenReturn(keycloakAuthenticationToken);
  }

  @Test
  void when_valid_uuid_in_accessToken_then_uuid_found() {
    Mockito.when(accessToken.getSubject()).thenReturn(UUID.randomUUID().toString());

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertTrue(optionalUUID.isPresent());
  }

  @Test
  void when_empty_uuid_in_accessToken_then_uuid_not_found() {
    Mockito.when(accessToken.getSubject()).thenReturn("");

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertFalse(optionalUUID.isPresent());
  }

  @Test
  void when_blank_uuid_in_accessToken_then_uuid_not_found() {
    Mockito.when(accessToken.getSubject()).thenReturn("   \n   \t       ");

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertFalse(optionalUUID.isPresent());
  }

  @Test
  void when_invalid_uuid_in_accessToken_then_uuid_not_found() {
    Mockito.when(accessToken.getSubject()).thenReturn("573a2d77-917544f3-8170-7b60923c53b7");

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertFalse(optionalUUID.isPresent());
  }
}
