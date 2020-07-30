package com.step902020.capstone;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDateTime;

import com.step902020.capstone.security.CurrentUser;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
 * TODO: filtering, creating event with front end input
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

  @GetMapping("get-filtered-events")
  public List<Event> getFilteredEvents(
          @RequestParam("foodAvailable") Boolean foodAvailable,
          @RequestParam("requiredFee") Boolean requiredFee) throws IOException {
    // False values changed to null for matching
    foodAvailable = foodAvailable == false ? null: foodAvailable;
    requiredFee = requiredFee == false ? null: requiredFee;

    Iterable<Event> events = this.eventRepository.findAll(
            Example.of(new Event(null, null, 0, null,
                            null, null, 0,
                            0, foodAvailable, requiredFee),
                    ExampleMatcher.matching().withIgnorePaths("datastoreId", "organizationId", "eventLatitude", "eventLongitude")
            )

    );
    List<Event> noPastEvents = new ArrayList<Event>();
    LocalDateTime now = LocalDateTime.now();
    for (Event e : events) {
      LocalDateTime eventDate = LocalDateTime.parse(e.getEventDateTime());
      if (eventDate.compareTo(now) >= 0) {
        noPastEvents.add(e);
      }
    }
    return noPastEvents;
  }

  @GetMapping("get-map-events")
  public Iterable<Event> getMapEvents() {
    return this.eventRepository.findAll();
  }

  @GetMapping("get-university-map")
  public University getUniversityMap(CurrentUser user) throws IOException {
    Organization organization = this.organizationRepository.findFirstByEmail(user.getEmail());
    return organization.getUniversity();
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
     @RequestParam("foodAvailable") Optional<Boolean> foodAvailable,
     @RequestParam("requiredFee") Optional<Boolean> requiredFee,
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
        event.setFoodAvailable(foodAvailable.orElse(false));
        event.setRequiredFee(requiredFee.orElse(false));
        this.eventRepository.save(event);
      } else {
        Event newEvent = new Event(organization.getUniversity(), organization.getName(), organization.getDatastoreId(), eventTitle, eventDateTime,
                eventDescription, Double.parseDouble(eventLatitude), Double.parseDouble(eventLongitude),
                foodAvailable.orElse(false), requiredFee.orElse(false));
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
  @PostMapping("/new-review")
  public List<Review> addReview(
          CurrentUser user,
          @RequestParam("text") String text,
          @RequestParam("eventId") Long eventId) throws IOException {

    Event event = this.eventRepository.findById(eventId).get();
    Individual individual = this.individualRepository.findFirstByEmail(user.getEmail());
    String individualName = individual.firstName + " " + individual.lastName;
    String individualEmail = individual.email;
    Review review = new Review(individualName, individualEmail, text);
    event.addReview(review);
    this.eventRepository.save(event);
    return event.reviews;
  }
}
