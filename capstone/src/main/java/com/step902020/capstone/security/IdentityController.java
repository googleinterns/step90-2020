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
  public Map<String, String> getUserType(CurrentUser user)  {
    Organization organization = this.organizationRepository.findByEmail(user.getEmail()).orElse(null);
    Map<String, String> userInfo = new HashMap<String, String>();
    if (organization != null) {
      userInfo.put("userType", "organization");
      userInfo.put("university", organization.getUniversity());
    } else {
      Individual individual = this.individualRepository.findByEmail(user.getEmail()).orElse(null);
      if (individual != null) {
        userInfo.put("userType", "individual");
        userInfo.put("university", individual.getUniversity());
      }
      else {
        userInfo.put("userType", "");
        userInfo.put("university", "");
      }
    }
    return userInfo;
  }
}
