
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
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

  @Autowired
  private EventRepository eventRepository;
  
  /**
   * Find an organization's profile information by email
   * @param email get the user email from IAP headers
   * @return list of organizations with the same email as param
   */
  @GetMapping("get-organization")
  public List<Organization> getOrganization(CurrentUser currentUser) {
    return this.organizationRepository.findByEmail(currentUser.getEmail());
  }
  
  /**
   * Save organization information into Datastore. If the email does not yet exist in
  Datastore, create a new entity. Otherwise do an update on the existing entity
   * @param name name of the organization
   * @param email email of the current user from IAP header
   * @param userType type of user, either individual or organization
   * @param university affiliated university
   * @param description short bio
   * @return RedirectView to profile.html
   * @throws IOException
   */
  @PostMapping("save-organization")
  public RedirectView saveOrganization(
      @RequestParam("name") String name,
      CurrentUser user,
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university,
      @RequestParam("description") String description) throws IOException {

    String userEmail = user.getEmail();
    Organization current = null;
    List<Organization> orgList = this.organizationRepository.findByEmail(userEmail);
    
    // either edit the existing user or create a new one
    if (orgList.size() > 0) {
      current = orgList.get(0);
      current.setName(name);
      current.setDescription(description);
    } else {
      current = new Organization(System.currentTimeMillis(), name, userEmail, university, userType, description, "");
    }
    this.organizationRepository.save(current);
    return new RedirectView("profile.html", true);
  }

  /**
   * Find organizations that matches the current search input through prefix matching
   * @param name input entered into the search
   * @param university university of the current user
   * @return list of organizations with names containing prefix of the input
   * @throws IOException
   */
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

  /**
   * Find an organization's profile information by email
   * @param organizationId id of the current user
   * @return Organization object
   */
  @GetMapping("get-public-profile")
  public Organization getPublicProfile(@RequestParam("organization-id") String organizationId) throws IOException {
    return this.organizationRepository.findById(Long.parseLong(organizationId)).orElse(null);
  }

  /**
   * deletes event from the organization's event list and deletes the event entity
   * @param eventId datastore id of the event to be deleted
   * @param user current user
   * @return RedirectView to manage event page
   * @throws IOException
   */
  @PostMapping("delete-organization-event")
  public RedirectView deleteOrganizationEvent(@RequestParam("event-id") String eventId, CurrentUser user) throws IOException {
    Organization current = this.organizationRepository.findByEmail(user.getEmail()).get(0);
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    if (event != null) {
      current.deleteEvent(event);
      this.eventRepository.deleteById(Long.parseLong(eventId));
    }
    return new RedirectView("manageevents.html", true);
  }
}