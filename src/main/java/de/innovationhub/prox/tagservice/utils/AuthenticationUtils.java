package de.innovationhub.prox.tagservice.utils;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

/** Provides some Authentication Utilities */
public interface AuthenticationUtils {

  /**
   * Method to obtain UUID of the requesting user from a HttpServletRequest
   *
   * @param request the request of which the UUID should be extracted
   * @return If extraction was successful it returns the UUID, otherwise a empty optional
   */
  Optional<UUID> getUserUUIDFromRequest(HttpServletRequest request);
}
