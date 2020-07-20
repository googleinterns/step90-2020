package com.step902020.capstone.security;

import org.springframework.cloud.gcp.security.iap.AudienceProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

  public String getEmail() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();

    String name;
    if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      name = jwt.getClaimAsString("email");
    } else {
      name = authentication.getName();
    }

    return name;
  }
}
