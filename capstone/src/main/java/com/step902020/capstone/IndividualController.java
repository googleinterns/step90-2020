
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


@RestController
public class IndividualController {

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private EventRepository eventRepository;

  /** Find an individual's profile information by email
   * @param email user's email from IAP header
   * @return list of individuals with the same email
   */
  @GetMapping("get-individual")
  public List<Individual> getIndividual(@RequestHeader("X-Goog-Authenticated-User-Email") Optional<String> email) {
    return this.individualRepository.findByEmail("jennysheng@google.com");
  }

  /** Save user information into Datastore. If the email does not yet exist in 
  Datastore, create a new entity. Otherwise do an update on the existing entity
   * @param firstname user's first name
   * @param lastname user's last name
   * @param email user's email from IAP header
   * @param userType user type, either individual or organization
   * @param university user's university
   * @return RedirectView to the profile.html
   * @throws IOException
   */
  @PostMapping("save-individual")
  public RedirectView saveIndividual(
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      @RequestHeader("X-Goog-Authenticated-User-Email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email.substring(20));
    
    // either edit the existing user or create a new one
    if (userList.size() > 0) {
      current = userList.get(0);
      current.setFirstName(firstname);
      current.setLastName(lastname);
    }
    else {
        current = new Individual(System.currentTimeMillis(), firstname, lastname, email.substring(20), university, userType, "");
    }
    this.individualRepository.save(current);
    return new RedirectView("profile.html", true);
  }

  /** Add the event with the event id to the current individual's list of saved events
   * @param eventId datastoreId of the event being added
   * @param email email of the user from IAP header
   * @return RedirectView to savedevents.html
   * @throws IOException
   */
  @PostMapping("add-saved-event")
  public RedirectView addSavedEvent(
      @RequestParam("event-id") String eventId,
      @RequestHeader("X-Goog-Authenticated-User-Email") String email) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email.substring(20));
    if (userList.size() > 0) {
      current = userList.get(0);
      current.addSavedEvents(Long.parseLong(eventId));
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }
  
  /** delete the event with the event id from the current individual's list of saved events
   * @param eventId datastoreId of the event being deleted
   * @param email email of the user from IAP header
   * @return RedirectView to savedevents.html
   * @throws IOException
   */
  @PostMapping("delete-saved-event")
  public RedirectView deleteSavedEvent(
      @RequestParam("event-id") String eventId,
      @RequestHeader("X-Goog-Authenticated-User-Email") String email) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email.substring(20));
    if (userList.size() > 0) {
      current = userList.get(0);
      current.deleteSavedEvents(Long.parseLong(eventId));
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }

  /** Add the organization with the organization id to the current 
  individual's list of saved organizations
   * @param email email of the user from IAP header
   * @param organizationId datastoreId of the organization being added
   * @return RedirectView to savedorganizations.html
   * @throws IOException
   */
  @PostMapping("add-saved-organization")
  public RedirectView addSavedOrganizations(
      @RequestHeader("X-Goog-Authenticated-User-Email") String email,
      @RequestParam("organization-id") String organizationId) throws IOException {
    
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email.substring(20));
    if (userList.size() > 0) {
      current = userList.get(0);
      current.addSavedOrganizations(Long.parseLong(organizationId));
    } 
    this.individualRepository.save(current);
    return new RedirectView("organizationsearch.html", true);
  }

   /** delete the organization with the organization id from the current 
  individual's list of saved organizations
   * @param email email of the user from IAP headers
   * @param organizationId datastoreId of the organization being deleted
   * @return RedirectView to savedorganizations.html
   * @throws IOException
   */
  @PostMapping("delete-saved-organization")
  public RedirectView deleteSavedOrganization(
      @RequestHeader("X-Goog-Authenticated-User-Email") String email,
      @RequestParam("organization-id") String organizationId) throws IOException {
    
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email.substring(20));
    if (userList.size() > 0) {
      current = userList.get(0);
      current.deleteSavedOrganizations(Long.parseLong(organizationId));
    } 
    this.individualRepository.save(current);
    return new RedirectView("savedorganizations.html", true);
  }

  /**
   * gets a combined list of events from the user's saved list of events and events from all the
   * saved organizations
   * @return Iterable containing Events
   * @throws IOException
   */
  @GetMapping("get-calendar-events")
  public Iterable<Event> getCalendarEvents() throws IOException {
    /* full implementation blocked by the previous PR (because I changed some properties into references )
     and those changes are not in this branch */
    // get saved events
    // get a list of organizations
    // for each of the organizations get their list of saved events

    return this.eventRepository.findAll();
  }
}