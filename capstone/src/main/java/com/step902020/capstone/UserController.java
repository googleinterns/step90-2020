
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
public class UserController {

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @GetMapping("get-individual")
  public List<Individual> getIndividual(@RequestParam("email") String email) {
    return this.individualRepository.findByEmail(email);
  }

  @GetMapping("get-organization")
  public List<Organization> getOrganization(@RequestParam("email") String email) {
    return this.organizationRepository.findByEmail(email);
  }

  @PostMapping("save-individual")
  public RedirectView saveIndividual(
      @RequestParam("id") String id,
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      @RequestParam("email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university) throws IOException {

    // each email can only exist as either an individual or a user, not both
    this.organizationRepository.deleteByEmail(email);
    
    Individual current = null;
    // depending on whether there is an id, either update or insert a new entity
    if (id.length() == 0) {
      current = new Individual(System.currentTimeMillis(), firstname, lastname, email, university, userType, "");
    } else {
      current = new Individual(Long.parseLong(id), System.currentTimeMillis(), firstname, lastname, email, university, userType, "");
    }
    this.individualRepository.save(current);
    return new RedirectView("profile.html", true);
  }

  @PostMapping("save-organization")
  public RedirectView saveOrganization(
      @RequestParam("id") String id,
      @RequestParam("name") String name,
      @RequestParam("email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university,
      @RequestParam("description") String description) throws IOException {
    
    // each email can only exist as either an individual or a user, not both
    this.individualRepository.deleteByEmail(email);

    Organization current = null;
    // depending on whether there is an id, either update or insert a new entity
    if (id.length() == 0) {
      current = new Organization(System.currentTimeMillis(), name, email, university, userType, description, "");
    } else {
      current = new Organization(Long.parseLong(id), System.currentTimeMillis(), name, email, university, userType, description, "");
    }
    this.organizationRepository.save(current);
    return new RedirectView("profile.html", true);
  }
}