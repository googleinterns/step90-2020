package com.step902020.capstone.security;

import com.step902020.capstone.Individual;
import com.step902020.capstone.IndividualRepository;
import com.step902020.capstone.Organization;
import com.step902020.capstone.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IdentityController {

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @GetMapping("identity")
  public String getIdentity(CurrentUser user) {
    return user.getEmail();
  }

  @GetMapping("user-info")
  public Object getUserType(CurrentUser user)  {
    Organization organization = this.organizationRepository.findFirstByEmail(user.getEmail());
    if (organization != null) {
      return organization;
    } else {
      Individual individual = this.individualRepository.findFirstByEmail(user.getEmail());
      if (individual != null) {
        return individual;
      } else {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userType", "unknown");
        return map;
      }
    }
  }
}
