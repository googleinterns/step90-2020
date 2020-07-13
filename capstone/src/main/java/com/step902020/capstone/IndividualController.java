
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.util.List;
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

  /** Find currently logged in individual. */
  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  /**
   * Find an individual's profile information by email
   * @param user Currently logged-in user
   * @return list of individuals with the same email
   */
  @GetMapping("get-individual")
  public List<Individual> getIndividual(CurrentUser user) {
    return this.individualRepository.findByEmail(user.getEmail());
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
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(userEmail);
    
    // either edit the existing user or create a new one
    if (userList.size() > 0) {
      current = userList.get(0);
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

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    if (userList.size() > 0) {
      current = userList.get(0);
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

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    if (userList.size() > 0) {
      current = userList.get(0);
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
    
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    Organization organization = this.organizationRepository.findById(Long.parseLong(organizationId)).orElse(null);
    if (userList.size() > 0) {
      current = userList.get(0);
      current.addOrganizations(organization);
    } 
    this.individualRepository.save(current);
    return new RedirectView("organizationsearch.html", true);
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
    
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    Organization organization = this.organizationRepository.findById(Long.parseLong(organizationId)).orElse(null);

    if (userList.size() > 0) {
      current = userList.get(0);
      current.deleteOrganizations(organization);
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