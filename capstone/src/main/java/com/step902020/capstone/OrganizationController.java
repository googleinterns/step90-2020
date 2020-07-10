
package com.step902020.capstone;

import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import java.io.IOException;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@RestController
public class OrganizationController {

  @Autowired
  private OrganizationRepository organizationRepository;
  
  /** Find an organization's profile information by email */
  @GetMapping("get-organization")
  public List<Organization> getOrganization(@RequestHeader("X-Goog-Authenticated-User-Email") Optional<String> email) {
    return this.organizationRepository.findByEmail("jennysheng@google.com");
  }
  
  /** Get the profile information of the list of saved organizations by id*/
  @GetMapping("get-saved-organizations")
  public List<Organization> getSavedOrganizations(@RequestParam("emails") List<String> organizationIds) {
    List<Organization> result = new ArrayList<Organization>();

    // there should be a better way to do this using findByAllEmail() but I keep getting a Blob error
    for (String id : organizationIds) {
        Optional<Organization> org = this.organizationRepository.findById(Long.parseLong(id));
        if (org.isPresent()) {
            result.add(org.get());
        }
    }
    Collections.sort(result, new Comparator<Organization>() {
      @Override
      public int compare(Organization a, Organization b) {
        return a.getName().compareTo(b.getName());
      }
    });
    return result;
  }
  
  /** Save organization information into Datastore. If the email does not yet exist in 
  Datastore, create a new entity. Otherwise do an update on the existing entity */
  @PostMapping("save-organization")
  public RedirectView saveOrganization(
      @RequestParam("name") String name,
      @RequestHeader("X-Goog-Authenticated-User-Email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university,
      @RequestParam("description") String description) throws IOException {
    
    Organization current = null;
    List<Organization> orgList = this.organizationRepository.findByEmail(email.substring(20));
    
    // either edit the existing user or create a new one
    if (orgList.size() > 0) {
      current = orgList.get(0);
      current.setName(name);
      current.setDescription(description);
    } else {
      current = new Organization(System.currentTimeMillis(), name, email.substring(20), university, userType, description, "");
    }
    this.organizationRepository.save(current);
    return new RedirectView("profile.html", true);
  }

  @GetMapping("search-organization")
  public List<Organization> searchOrganization(
      @RequestParam("name") String name, 
      @RequestParam("university") String university) throws IOException {
    
    if (name.equals("")) {
        return this.organizationRepository.findByUniversity(university);
    } else {
        return this.organizationRepository.findOrganizationsByNameMatching(name, name + "\ufffd", university);
    } 
  }
}