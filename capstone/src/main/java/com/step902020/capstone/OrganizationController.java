
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private GcsStore gcsstore;
  
  /**
   * Find an organization's profile information by email
   * @param currentUser current user
   * @return organization with the same email as param
   */
  @GetMapping("get-organization")
  public Organization getOrganization(CurrentUser currentUser) {
    return this.organizationRepository.findFirstByEmail(currentUser.getEmail());
  }
  
  /**
   * Save organization information into Datastore. If the email does not yet exist in
  Datastore, create a new entity. Otherwise do an update on the existing entity
   * @param name name of the organization
   * @param user current user
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
      @RequestParam("description") String description,
      @RequestParam("org-type") String orgType) throws IOException {

    Organization current = getOrganization(user);
    
    // either edit the existing user or create a new one
    if (current != null) {
      current.setName(name);
      current.setDescription(description);
      current.setOrgType(orgType);
    } else {
      University universityReference = this.universityRepository.findFirstByName(university);
      current = new Organization(System.currentTimeMillis(), name, user.getEmail(), universityReference, userType, description, orgType);
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
      @RequestParam("university") String university,
      @RequestParam("orgTypes") List<String> orgTypes) throws IOException {
    University universityReference = this.universityRepository.findFirstByName(university);
    if (name.equals("")) {
      if (orgTypes.isEmpty()) {
        return this.organizationRepository.findByUniversity(universityReference);
      } else {
        List<Organization> filteredOrgs = new ArrayList();
        for(String orgType: orgTypes) {
          filteredOrgs.addAll(this.organizationRepository.findByUniversityAndOrgType(universityReference, orgType));
        }
        return filteredOrgs;
      }
    } else {
        return this.organizationRepository.findOrganizationsByNameMatching(name, name + "\ufffd", universityReference);
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
    Organization current = getOrganization(user);
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    if (event != null) {
      current.deleteEvent(event);
      this.eventRepository.deleteById(Long.parseLong(eventId));
    }
    return new RedirectView("manageevents.html", true);
  }

  /**
   * get the recommended events for an organization
   * @param currentUser user that is currently logged in
   * @param count the number of events to be returned
   * @return list of Events
   */
  @GetMapping("get-recommended-events-organization")
  public List<Event> recommendEvents(CurrentUser currentUser,
                                     @RequestParam("count") String count) {
    Organization targetUser = this.organizationRepository.findFirstByEmail(currentUser.getEmail());
    List<Event> allEvents = null;
    if (count.equals("All")) {
      allEvents = this.eventRepository.findByUniversityAndEventDateTimeGreaterThan(targetUser.getUniversity(), LocalDateTime.now().toString());
    } else {
      allEvents = this.eventRepository.findByUniversityAndEventDateTimeGreaterThan(targetUser.getUniversity(), LocalDateTime.now().toString(), PageRequest.of(0, Integer.parseInt(count)));
    }
    return allEvents;
  }

  /**
   * get the recommended organizations for an organization
   * @param currentUser user that is currently logged in
   * @param count the number of organizations to be returned
   * @return list of organizations
   */
  @GetMapping("get-recommended-organizations-organization")
  public List<Organization> recommendedOrganizations(CurrentUser currentUser,
                                     @RequestParam("count") String count) {
    Organization targetUser = this.organizationRepository.findFirstByEmail(currentUser.getEmail());
    int num = Integer.parseInt(count);
    List<Organization> allOrgs = null;
    if (count.equals("All")) {
      allOrgs = this.organizationRepository.findByUniversity(targetUser.getUniversity());
    } else {
      allOrgs = this.organizationRepository.findByUniversity(targetUser.getUniversity(), PageRequest.of(0, num));
    }
    return allOrgs;
  }

  /**
   * returns image with the same name as the user email from cloud storage
   * @param email of the current user
   * @return image in a byte array
   * @throws IOException
   */
  @GetMapping(value = "get-public-image", produces = MediaType.IMAGE_JPEG_VALUE)
  public @ResponseBody byte[] getImage(@RequestParam("email") String email) throws IOException {
    return gcsstore.serveImage("step90-2020", "step90-2020.appspot.com", email);
  }
}