package com.step902020.capstone;

import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
  private OrganizationRepository organizationRepository;

  @GetMapping("get-all-events")
  public Iterable<Event> getAllEvents() {
    return this.eventRepository.findAll();
  }

  @GetMapping("get-event")
  public Event getEvent(@RequestParam("event-id") String eventId) throws IOException {
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    return event;
  }

  @PostMapping("save-event")
  public RedirectView saveEvent (
     @RequestHeader("X-Goog-Authenticated-User-Email") String email,
     @RequestParam("eventTitle") String eventTitle,
     @RequestParam("eventDateTime") String eventDateTime,
     @RequestParam("eventDescription") String eventDescription,
     @RequestParam("eventLatitude") String eventLatitude,
     @RequestParam("eventLongitude") String eventLongitude,
     @RequestParam("foodAvaliable") Optional<Boolean> foodAvaliable,
     @RequestParam("requiredFee") Optional<Boolean> requiredFee,
     @RequestParam("event-id") String eventId
    ) throws IOException {
      Organization organization = organizationRepository.findByEmail(email.substring(20)).get(0);
      Event event = eventId.length() <= 0? null : this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
      if (event != null) {
        event.setEventDateTime(eventDateTime);
        event.setEventDescription(eventDescription);
        event.setEventLatitude(Double.parseDouble(eventLatitude));
        event.setEventLongitude(Double.parseDouble(eventLongitude));
        event.setEventTitle(eventTitle);
        event.setOrganization(organization);
        event.setFoodAvalible(foodAvaliable);
        event.setRequiredFee(requiredFee);
        this.eventRepository.save(event);
      } else {
        Event newEvent = new Event(organization, eventTitle, eventDateTime, eventDescription, Double.parseDouble(eventLatitude), Double.parseDouble(eventLongitude), foodAvaliable.orElse(false), requiredFee.orElse(false));
        this.eventRepository.save(newEvent);
        organization.addEvent(newEvent);
        this.organizationRepository.save(organization);
      }
      return new RedirectView("manageevents.html", true);
  }

  @PostMapping("/new-review")
  public void addReview(
          @RequestParam("text") String text,
          @RequestParam("eventId") Long eventId,
          @RequestParam("name") String name) throws IOException {

    Event event = this.eventRepository.findById(eventId).get();

    event.addReview(new Review(text, name));
    this.eventRepository.save(event);
  }
}
