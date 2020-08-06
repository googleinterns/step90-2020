
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.time.LocalDateTime;
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
  private UniversityRepository universityRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private GcsStore gcsstore;

  private Recommender recommender = new Recommender();
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
      University universityReference = this.universityRepository.findFirstByName(university);
      current = new Individual(System.currentTimeMillis(), firstname, lastname, userEmail, universityReference, userType);
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
      event.incrementRank();
      this.eventRepository.save(event);
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
      event.decrementRank();
      this.eventRepository.save(event);
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
      organization.incrementRank();
      this.organizationRepository.save(organization);
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
      organization.decrementRank();
      this.organizationRepository.save(organization);
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
  @GetMapping("get-all-org-events")
  public Set<Event> getCalendarEvents(CurrentUser user) throws IOException {
    // get saved events and for each of the organizations get their list of saved events
    Individual current = getIndividual(user);
    Set<Event> calendarEvents = new HashSet<Event>(current.getSavedEvents());
    if (current != null) {
      for (Organization org : current.getOrganizations()) {
        for (Event event : org.getEvents()) {
          if (!current.getSavedEvents().contains(event)) {
            calendarEvents.add(event);
          }
        }
      }
    }
    return calendarEvents;
  }

  /**
   * get the recommended events for an individual. The algorithm
   * used is user-based collaborative filtering, through which
   * the target user is compared in terms of "distance" to every
   * other user that is in the same university. Then the list of users
   * is sorted by ascending distance, and their list of saved events
   * are added to the list to be returned. The distance is determined
   * by the number of dissimilar events that the two users have.
   * If the number of events is shorter than the count,
   * add other events in the same university to fill the list.
   * @param currentUser user that is currently logged in
   * @param count the number of events to be returned
   * @return list of Events
   */
  @GetMapping("get-recommended-events-individual")
  public List<Event> recommendEvents(CurrentUser currentUser,
                                @RequestParam("count") String count) {
    Individual targetUser = this.individualRepository.findFirstByEmail(currentUser.getEmail());
    List<Individual> users = this.individualRepository.findByUniversity(targetUser.getUniversity());

    int num = (count.equals("All")) ? Integer.MAX_VALUE : Integer.parseInt(count);
    // find recommended events from other users
    List<Event> recommended = recommender.recommend(targetUser, users, u -> u.getSavedEvents(), num);
    // filter out the past events
    List<Event> noPastEvents = new ArrayList<Event>();
    LocalDateTime now = LocalDateTime.now();
    for (Event e : recommended) {
      LocalDateTime eventDate = LocalDateTime.parse(e.getEventDateTime());
      if (eventDate.compareTo(now) >=0) {
        noPastEvents.add(e);
      }
    }

    /* if the list of recommended events is shorter than the list we want, add in more events from general event pool
    according to descending popularity */
    if (noPastEvents.size() < num) {
      List<Event> allEvents = this.eventRepository.findByUniversityAndEventDateTimeGreaterThanOrderByEventDateTimeAscRankDesc(targetUser.getUniversity(), now.toString());
      int i = 0;
      int numAlreadyAdded = 0;
      int targetSize = noPastEvents.size();
      int numExtraEvents = num - targetSize;
      while (i < allEvents.size() && numAlreadyAdded < numExtraEvents) {
        Event e = allEvents.get(i);
        if (!(noPastEvents.contains(e))) {
          noPastEvents.add(e);
          numAlreadyAdded++;
        }
        i++;
      }
    }
    return noPastEvents;
  }

  /**
   * get the recommended organization for an individual. The algorithm
   * used is user-based collaborative filtering, through which
   * the target user is compared in terms of "distance" to every
   * other user that is in the same university. Then the list of users
   * is sorted by ascending distance, and their list of saved organizations
   * are added to the list to be returned. The distance is determined
   * by the number of dissimilar organizations that the two users have.
   * If the number of organizations is shorter than the count,
   * add other organizations in the same university to fill the list.
   * @param currentUser user that is currently logged in
   * @param count the number of organizations to be returned
   * @return list of organizations
   */
  @GetMapping("get-recommended-organizations-individual")
  public List<Organization> reommendedOrganizations(CurrentUser currentUser,
                                @RequestParam("count") String count) {
    Individual targetUser = this.individualRepository.findFirstByEmail(currentUser.getEmail());
    List<Individual> users = this.individualRepository.findByUniversity(targetUser.getUniversity());

    int num = (count.equals("All")) ? Integer.MAX_VALUE : Integer.parseInt(count);
    // find recommended events from other users
    List<Organization> recommended = recommender.recommend(targetUser, users, u -> u.getOrganizations(), num);
    // if the list of recommended events is shorter than the list we want, add in more events from general event pool
    if (recommended.size() < num) {
      List<Organization> allOrgs = this.organizationRepository.findByUniversityOrderByRankDesc(targetUser.getUniversity());
      int i = 0;
      int numAlreadyAdded = 0;
      int targetSize = recommended.size();
      int numExtraEvents = num - targetSize;
      while (i < allOrgs.size() && numAlreadyAdded < numExtraEvents) {
        Organization e = allOrgs.get(i);
        if (!(recommended.contains(e))) {
          recommended.add(e);
          numAlreadyAdded++;
        }
        i++;
      }
    }

    return recommended;
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

  /**
   * sets the recommender to be used for creating recommendations
   * @param newRecommender recommender to be used
   */
  public void setRecommender(Recommender newRecommender) {
    recommender = newRecommender;
  }

  /**
   * Add new review to event
   * @param user current user
   * @param text review's text
   * @param orgId organization's datastore id
   * @return updated review list
   */
  @PostMapping("/add-org-review")
  public Organization addReview(
          CurrentUser user,
          @RequestParam("text") String text,
          @RequestParam("reviewedObjectId") Long orgId) throws IOException {

    Organization organization = this.organizationRepository.findById(orgId).get();
    Individual individual = this.individualRepository.findFirstByEmail(user.getEmail());
    String individualName = individual.firstName + " " + individual.lastName;
    String individualEmail = individual.email;
    Review review = new Review(individualName, individualEmail, text);
    organization.addReview(review);
    this.organizationRepository.save(organization);
    return organization;
  }

  /**
   * Remove review the event
   * Only author of review can delete review
   * @param user current user
   * @param reviewId review's datastore id
   * @param orgId organization's datastore id
   * @return updated review list
   */
  @PostMapping("/remove-org-review")
  public void removeReview(
          CurrentUser user,
          @RequestParam("reviewId") Long reviewId,
          @RequestParam("reviewedObjectId") Long orgId) throws IOException {

    Review review = this.reviewRepository.findById(reviewId).get();
    if (review.individualEmail.equals(user.getEmail())) {
      this.reviewRepository.delete(review);
      Organization organization = this.organizationRepository.findById(orgId).get();
      organization.removeReview(review);
      this.organizationRepository.save(organization);
    }
  }
}