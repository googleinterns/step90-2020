package com.step902020.capstone;

import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

  @PostMapping("save-event")
  public RedirectView saveEvent (
    @RequestParam("organizationId") Long organizationId,
    @RequestParam("eventTitle") String eventTitle,
    @RequestParam("eventDateTime") String eventDateTime,
    @RequestParam("eventDescription") String eventDescription,
    @RequestParam("eventLatitude") String eventLatitude,
    @RequestParam("eventLongitude") String eventLongitude,
    @RequestParam("foodAvaliable") Optional<Boolean> foodAvaliable,
    @RequestParam("requiredFee") Optional<Boolean> requiredFee
    ) throws IOException {
      
      Organization organization = organizationRepository.findById(organizationId).orElse(null);
      
      Event newEvent = new Event(organization, eventTitle, eventDateTime, eventDescription, Double.parseDouble(eventLatitude), Double.parseDouble(eventLongitude), foodAvaliable.orElse(false), requiredFee.orElse(false));

      this.eventRepository.save(newEvent);
      return new RedirectView("event.html", true);
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
