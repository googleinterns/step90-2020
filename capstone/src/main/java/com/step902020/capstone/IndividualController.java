
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
public class IndividualController {

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private GcsStore gcsstore;

  /**
   * Find an individual's profile information by email
   * @param user Currently logged-in user
   * @return individual with the same email
   */
  @GetMapping("get-individual")
  public Individual getIndividual(CurrentUser user) {
    return this.individualRepository.findFirstByEmail(user.getEmail());
  }

  /** Save user information into Datastore. If the email does not yet exist in 
  Datastore, create a new entity. Otherwise do an update on the existing entity */
  @PostMapping("save-individual")
  public RedirectView saveIndividual(
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      CurrentUser user,
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university) throws IOException {

    String userEmail = user.getEmail();
    Individual current = getIndividual(user);
    
    // either edit the existing user or create a new one
    if (current != null) {
      current.setFirstName(firstname);
      current.setLastName(lastname);
    }
    else {
        current = new Individual(System.currentTimeMillis(), firstname, lastname, userEmail, university, userType);
    }
    this.individualRepository.save(current);
    return new RedirectView("profile.html", true);
  }

  /**
   * Add the event with the event id to the current individual's list of saved events
   * @param eventId datastoreId of the event being added
   * @param user Currently logged-in user
   * @return RedirectView to savedevents.html
   * @throws IOException
   */
  @PostMapping("add-saved-event")
  public RedirectView addSavedEvent(
      @RequestParam("event-id") String eventId,
      CurrentUser user) throws IOException {

    Individual current = getIndividual(user);
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    if (current != null) {
      current.addSavedEvents(event);
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }
  
  /**
   * delete the event with the event id from the current individual's list of saved events
   * @param eventId datastoreId of the event being deleted
   * @param user Currently logged-in user
   * @return RedirectView to savedevents.html
   * @throws IOException
   */
  @PostMapping("delete-saved-event")
  public RedirectView deleteSavedEvent(
      @RequestParam("event-id") String eventId,
      CurrentUser user) throws IOException {

    Individual current = getIndividual(user);
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    if (current != null) {
      current.deleteSavedEvents(event);
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }

  /**
   * Add the organization with the organization id to the current
  individual's list of saved organizations
   * @param user Currently logged-in user
   * @param organizationId datastoreId of the organization being added
   * @return RedirectView to savedorganizations.html
   * @throws IOException
   */
  @PostMapping("add-saved-organization")
  public RedirectView addSavedOrganizations(
      CurrentUser user,
      @RequestParam("organization-id") String organizationId) throws IOException {
    
    Individual current = getIndividual(user);
    Organization organization = this.organizationRepository.findById(Long.parseLong(organizationId)).orElse(null);
    if (current != null) {
      current.addOrganizations(organization);
    } 
    this.individualRepository.save(current);
    return new RedirectView("savedorganizations.html", true);
  }

  /**
   * delete the organization with the organization id from the current
  individual's list of saved organizations
   * @param user Currently logged-in user
   * @param organizationId datastoreId of the organization being deleted
   * @return RedirectView to savedorganizations.html
   * @throws IOException
   */
  @PostMapping("delete-saved-organization")
  public RedirectView deleteSavedOrganization(
      CurrentUser user,
      @RequestParam("organization-id") String organizationId) throws IOException {
    
    Individual current = getIndividual(user);
    Organization organization = this.organizationRepository.findById(Long.parseLong(organizationId)).orElse(null);

    if (current != null) {
      current.deleteOrganizations(organization);
    } 
    this.individualRepository.save(current);
    return new RedirectView("savedorganizations.html", true);
  }

  /**
   * gets a combined list of events from the user's saved list of events and events from all the
   * saved organizations
   * @param user Currently logged-in user
   * @return list of events
   * @throws IOException
   */
  @GetMapping("get-calendar-events")
  public Set<Event> getCalendarEvents(CurrentUser user) throws IOException {
    // get saved events and for each of the organizations get their list of saved events
    Individual current = getIndividual(user);
    Set<Event> calendarEvents = new HashSet<Event>();
    if (current != null) {
      calendarEvents.retainAll(current.getSavedEvents());
      for (Organization org : current.getOrganizations()) {
        calendarEvents.addAll(org.getEvents());
      }
    }
    return calendarEvents;
  }

  /**
   * return the html form of the profile image form with a valid upload url
   * @param user current user
   * @return html in a String
   */
  @GetMapping("upload-image")
  public String uploadImage(CurrentUser user) throws IOException{
    return gcsstore.generateSignedPostPolicyV4("step90-2020", "step90-2020.appspot.com", user.getEmail());
  }

  /**
   * returns image with the same name as the user email from cloud storage
   * @param user current user
   * @return image in a byte array
   * @throws IOException
   */
  @GetMapping(value = "get-image", produces = MediaType.IMAGE_JPEG_VALUE)
  public @ResponseBody byte[] getImage(CurrentUser user) throws IOException {
    return gcsstore.serveImage("step90-2020", "step90-2020.appspot.com", user.getEmail());
  }
}