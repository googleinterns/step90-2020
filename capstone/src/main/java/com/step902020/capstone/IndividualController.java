
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;


@RestController
public class IndividualController {

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

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
        current = new Individual(System.currentTimeMillis(), firstname, lastname, userEmail, university, userType, "");
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
   * get the recommended events for an individual
   * @param currentUser user that is currently logged in
   * @return list of Events
   */
  @GetMapping("get-recommended-events-individual")
  public List<Event> recommendEvents(CurrentUser currentUser) {
    Individual targetUser = this.individualRepository.findFirstByEmail(currentUser.getEmail());
    List<Individual> users = this.individualRepository.findByUniversity(targetUser.getUniversity());
    List<Event> recommended = Recommender.recommend(targetUser, users, u -> u.getSavedEvents());
    if (recommended.size() < 10) {
      List<Event> allEvents = this.eventRepository.findAllByUniversity(targetUser.getUniversity());
      int i = 0;
      int length = 0;
      while (i < allEvents.size() && length < 10-recommended.size()) {
        Event e = allEvents.get(i);
        if (!(recommended.contains(e))) {
          recommended.add(e);
          length++;
        }
        i++;
      }
    }
    return recommended;
  }
}