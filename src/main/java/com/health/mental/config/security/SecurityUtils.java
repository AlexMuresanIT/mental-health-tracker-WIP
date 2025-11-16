package com.health.mental.config.security;

import jakarta.servlet.http.HttpServletRequest;

public final class SecurityUtils {
  private static final String DEFAULT_IP_ADDRESS_HEADER = "188.27.129.229";
  private static final String INVALID_IP_ADDRESS = "0:0:0:0:0:0:0:1";

  private SecurityUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static String getUserIpAddress(final HttpServletRequest request) {
    if (request == null) {
      return DEFAULT_IP_ADDRESS_HEADER;
    }
    final var ipAddress = request.getHeader("X-Forwarded-For");
    if (ipAddress == null || ipAddress.isEmpty()) {
      return INVALID_IP_ADDRESS.equals(request.getRemoteAddr())
          ? DEFAULT_IP_ADDRESS_HEADER
          : request.getRemoteAddr();
    }
    return ipAddress.split(",")[0];
  }
}
