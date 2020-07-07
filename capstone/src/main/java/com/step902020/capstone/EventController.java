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

  @GetMapping("get-all-events")
  public Iterable<Event> getAllEvents() {

    return this.eventRepository.findAll();
  }

  @GetMapping("get-single-event")
  public List<Event> getEventByID(@RequestParam("datastoreID") Long datastoreID) {
    return this.eventRepository.findByDatastoreID(datastoreID);
  }
//   @PostMapping("/new-event")
//   public void saveEvent() throws IOException {
//     this.eventRepository.save(new EventTemp("EVENT_HOLDER"));
//   }

//   @PostMapping("/new-review")
//   public void addReview(
//       @RequestParam("text") String text,
//       @RequestParam("eventId") long eventId,
//       @RequestParam("name") String name) throws IOException {
 
//   EventTemp event =this.eventRepository.findById(eventId);
//   LocalDateTime date = LocalDateTime.now();

//   event.addReview(new Review(date, text, name));
//   this.eventRepository.save(event);

//   }


  @PostMapping("save-event")
  public RedirectView saveEvent (
    @RequestParam("organizationName") String organizationName,
    @RequestParam("eventTitle") String eventTitle,
    @RequestParam("eventDateTime") String eventDateTime,
    @RequestParam("eventDescription") String eventDescription,
    @RequestParam("eventLatitude") String eventLatitude,
    @RequestParam("eventLongitude") String eventLongitude
    // @RequestParam("eventFilters") List<String> eventFilters
    ) throws IOException {
      
      Event newEvent = new Event(organizationName, eventTitle, eventDateTime, eventDescription, Double.parseDouble(eventLatitude), Double.parseDouble(eventLongitude));

      this.eventRepository.save(newEvent);
      return new RedirectView("event.html", true);

    }
  
    
}