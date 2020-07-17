package com.step902020.capstone;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

  @GetMapping("profile")
  public String profile() {
    return "profile";
  }

  @GetMapping("event")
  public String event() {
    return "event";
  }

  @GetMapping("manageevents")
  public String manageEvents() {
    return "manageevents";
  }

  @GetMapping("index")
  public String index() {
    return "index";
  }

  @GetMapping("map")
  public String map() {
    return "map";
  }

  @GetMapping("organizationsearch")
  public String organizationSearch() {
    return "organizationsearch";
  }

  @GetMapping("publicprofile")
  public String publicProfile() {
    return "publicprofile";
  }

  @GetMapping("savedevents")
  public String savedEvents() {
    return "savedevents";
  }

  @GetMapping("savedorganizations")
  public String savedOrganizations() {
    return "savedorganizations";
  }

  @GetMapping("usercalendar")
  public String userCalendar() {
    return "usercalendar";
  }
}

