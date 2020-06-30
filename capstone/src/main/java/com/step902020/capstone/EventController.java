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
import com.google.common.collect.Lists;

@RestController
public class EventController {

  @Autowired
  private EventRepository eventRepository;

  @GetMapping("/list-events")
  public Iterable<EventTemp> getEvents() {
    return this.eventRepository.findAll();
  }

  @PostMapping("/new-event")
  public void saveEvent() throws IOException {
    this.eventRepository.save(new EventTemp("EVENT_HOLDER"));
  }

  @PostMapping("/new-review")
  public void saveEvent(
      @RequestParam("text") String text,
      @RequestParam("id") long id) throws IOException {
    EventTemp event =this.eventRepository.findById(id);
    LocalDateTime date = LocalDateTime.now();

   event.addReview(new Review(date, text));
    this.eventRepository.save(event);
  }

}