package com.step902020.capstone.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentityController {

  @GetMapping("identity")
  public String getIdentity() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();

    String name;
    if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      name = jwt.getSubject();
    } else {
      name = authentication.getName();
    }

    return name;
  }
}
