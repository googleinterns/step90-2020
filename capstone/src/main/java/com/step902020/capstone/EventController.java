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

  @GetMapping("get-event")
  public Event getEvent(@RequestParam("event-id") String eventId) throws IOException {
    Event event = this.eventRepository.findById(Long.parseLong(eventId)).orElse(null);
    return event;
  }

  @PostMapping("save-event")
  public RedirectView saveEvent (
    @RequestParam("organizationId") Long organizationId,
    @RequestParam("eventTitle") String eventTitle,
    @RequestParam("eventDateTime") String eventDateTime,
    @RequestParam("eventDescription") String eventDescription,
    @RequestParam("eventLatitude") String eventLatitude,
    @RequestParam("eventLongitude") String eventLongitude
    ) throws IOException {
      
      Organization organization = organizationRepository.findById(organizationId).orElse(null);

      Event newEvent = new Event(organization, eventTitle, eventDateTime, eventDescription, Double.parseDouble(eventLatitude), Double.parseDouble(eventLongitude));

      this.eventRepository.save(newEvent);
      organization.addEvent(newEvent);
      this.organizationRepository.save(organization);
      return new RedirectView("event.html", true);

    }
  
    
}
