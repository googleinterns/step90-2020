package com.step902020.capstone;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDateTime;

import com.step902020.capstone.security.CurrentUser;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.step902020.capstone.security.IdentityController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.time.LocalDateTime;
import java.lang.Double;

import java.io.IOException;

/**
 * Event functionalities
 *   - Add new reviews
 *   - List reviews
 *   - Filters events
 *   - Create event
 *   - Update event
 */
 
@RestController
public class EventController {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private DatastoreTemplate datastoreTemplate;

  @GetMapping("get-all-events")
  public Iterable<Event> getAllEvent() throws IOException {
    return this.eventRepository.findAll();
  }

  /**
   * Find events that fit filters, have not happened, belong to user's university
   * @param universityName university name
   * @param eventTitle name of event
   * @param eventType event type
   * @param energyLevel amount of energy/excitement/work suggest for event
   * @param location is event indoors or outddors
   * @param foodAvailable if event provides food
   * @param free if event is free
   * @param visitorAllowed if visitors can attend event
   * @return list of valid events that fit selected filters
   */
  @GetMapping("get-filtered-events")
  public List<Event> getFilteredEvents(
          @RequestParam("universityName") String universityName,
          @RequestParam("eventTitle") String eventTitle,
          @RequestParam("eventType") String eventType,
          @RequestParam("energyLevel") String energyLevel,
          @RequestParam("location") String location,
          @RequestParam("foodAvailable") Boolean foodAvailable,
          @RequestParam("free") Boolean free,
          @RequestParam("visitorAllowed") Boolean visitorAllowed) throws IOException {
    Iterable<Event> events;
    University university = this.universityRepository.findFirstByName(universityName);

    // Can search with filters or by name -- cannot combine
    if (eventTitle.equals("")) {
      eventType = eventType.equals("") ? null: eventType;
      energyLevel = energyLevel.equals("") ? null: energyLevel;
      location = location.equals("") ? null: location;

      events = this.eventRepository.findAll(
          Example.of(new Event(null, null, 0, null, null,
                          null, 0, 0, eventType, null, location,
                          foodAvailable, free, visitorAllowed),
          ExampleMatcher.matching().withIgnorePaths("datastoreId", "organizationId", "eventLatitude", "eventLongitude", "requiredFee")));
    } else {
      events = this.eventRepository.findEventsByNameMatching(eventTitle, eventTitle + "\ufffd", university);
    }
    return getValidEvents(events, university.datastoreId);
  }

  @GetMapping("get-map-events")
  public Iterable<Event> getMapEvents() {
    return this.eventRepository.findAll();
  }

  @GetMapping("get-university-map")
  public University getUniversityMap(CurrentUser currentUser, @RequestParam String userType) throws IOException {

    Organization organization = this.organizationRepository.findFirstByEmail(currentUser.getEmail());
    Individual individual = this.individualRepository.findFirstByEmail(currentUser.getEmail());

    if(userType.equals("organization")) {
      return organization.getUniversity();
    } else {
        return individual.getUniversity();
    }
  }

  @GetMapping("get-event")
  public Event getEvent(@RequestParam("event-id") String eventId) throws IOException {
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    return event;
  }

  @PostMapping("save-event")
  public RedirectView saveEvent (
     CurrentUser user,
     @RequestParam("eventTitle") String eventTitle,
     @RequestParam("eventDateTime") String eventDateTime,
     @RequestParam("eventDescription") String eventDescription,
     @RequestParam("eventLatitude") String eventLatitude,
     @RequestParam("eventLongitude") String eventLongitude,
     @RequestParam("eventType") String eventType,
     @RequestParam("energyLevel") String energyLevel,
     @RequestParam("location") String location,
     @RequestParam("foodAvailable") Optional<Boolean> foodAvailable,
     @RequestParam("free") Optional<Boolean> free,
     @RequestParam("visitorAllowed") Optional<Boolean> visitorAllowed,
     @RequestParam("event-id") String eventId
    ) throws IOException {
      Organization organization = organizationRepository.findFirstByEmail(user.getEmail());
      Event event = eventId.length() <= 0? null : this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
      if (event != null) {
        event.setEventDateTime(eventDateTime);
        event.setEventDescription(eventDescription);
        event.setEventLatitude(Double.parseDouble(eventLatitude));
        event.setEventLongitude(Double.parseDouble(eventLongitude));
        event.setEventTitle(eventTitle);
        event.setOrganizationId(organization.getDatastoreId());
        event.setOrganizationName(organization.getName());
        event.setEventType(eventType);
        event.setEnergyLevel(energyLevel);
        event.setLocation(location);
        event.setFoodAvailable(foodAvailable.orElse(false));
        event.setFree(free.orElse(false));
        event.setVisitorAllowed(visitorAllowed.orElse(false));
        this.eventRepository.save(event);
      } else {
        Event newEvent = new Event(organization.getUniversity(), organization.getName(), organization.getDatastoreId(),
                eventTitle, eventDateTime, eventDescription, Double.parseDouble(eventLatitude),
                Double.parseDouble(eventLongitude), eventType, energyLevel, location,
                foodAvailable.orElse(false), free.orElse(false), visitorAllowed.orElse(false));
        this.eventRepository.save(newEvent);
        organization.addEvent(newEvent);
        this.organizationRepository.save(organization);
      }
      return new RedirectView("manageevents.html", true);
  }

  /**
   * Add new review to event
   * @param user current user
   * @param eventId Event's datastore id
   * @param text Review's text
   * @return Updated review list
   */
  @PostMapping("/new-event-review")
  public List<Review> addReview(
          CurrentUser user,
          @RequestParam("text") String text,
          @RequestParam("reviewedObjectId") Long eventId) throws IOException {

    Event event = this.eventRepository.findById(eventId).get();
    System.out.println(event);
    Individual individual = this.individualRepository.findFirstByEmail(user.getEmail());
    String individualName = individual.firstName + " " + individual.lastName;
    String individualEmail = individual.email;
    Review review = new Review(individualName, individualEmail, text);
    event.addReview(review);
    this.eventRepository.save(event);
    return event.reviews;
  }

  /**
   * Filter out past events and events from other universities
   * @param events list of events
   * @param universityId user's university's datastoreId
   * @return valid events
   * * FILTER UNIVERSITY MANUALLY DUE TO SMALL EVENT VOLUME*
   */
  private List<Event> getValidEvents(Iterable<Event> events, long universityId) {
    List<Event> validEvents = new ArrayList<Event>();
    LocalDateTime now = LocalDateTime.now();
    for (Event e : events) {
      if (e.university.datastoreId == universityId) {
        LocalDateTime eventDate = LocalDateTime.parse(e.getEventDateTime());
        if (eventDate.compareTo(now) >= 0) {
          validEvents.add(e);
        }
      }
    }
    return validEvents;
  }
}
