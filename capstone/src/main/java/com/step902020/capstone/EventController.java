package com.step902020.capstone;

import java.io.IOException;
import java.util.*;
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
    this.eventRepository.save(new EventTemp("hey"));
  }

}