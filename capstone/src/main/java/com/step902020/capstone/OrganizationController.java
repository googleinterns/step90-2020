
package com.step902020.capstone;

import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import java.io.IOException;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@RestController
public class OrganizationController {

  @Autowired
  private OrganizationRepository organizationRepository;

  @GetMapping("get-organization")
  public List<Organization> getOrganization(@RequestParam("email") String email) {
    return this.organizationRepository.findByEmail(email);
  }

  @PostMapping("save-organization")
  public RedirectView saveOrganization(
      @RequestParam("name") String name,
      @RequestParam("email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university,
      @RequestParam("description") String description) throws IOException {
    
    Organization current = null;
    List<Organization> orgList = this.organizationRepository.findByEmail(email);
    
    // either edit the existing user or create a new one
    if (orgList.size() > 0) {
      current = orgList.get(0);
      current.editName(name);
      current.editUniversity(university);
      current.editDescription(description);
    } else {
      current = new Organization(System.currentTimeMillis(), name, email, university, userType, description, "");
    }
    this.organizationRepository.save(current);
    return new RedirectView("profile.html", true);
  }
}