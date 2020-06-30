
package com.step902020.capstone;

import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import java.io.IOException;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@RestController
public class IndividualController {

  @Autowired
  private IndividualRepository individualRepository;

  @GetMapping("get-individual")
  public List<Individual> getIndividual(@RequestParam("email") String email) {
    return this.individualRepository.findByEmail(email);
  }


  @PostMapping("save-individual")
  public RedirectView saveIndividual(
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      @RequestParam("email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email);
    
    // either edit the existing user or create a new one
    if (userList.size() > 0) {
      current = userList.get(0);
      current.editFirstName(firstname);
      current.editLastName(lastname);
      current.editUniversity(university);
    }
    else {
        current = new Individual(System.currentTimeMillis(), firstname, lastname, email, university, userType, "");
    }
    this.individualRepository.save(current);
    return new RedirectView("profile.html", true);
  }


  @PostMapping("add-saved-event")
  public RedirectView addSavedEvent(
      @RequestParam("event-name") String eventName,
      @RequestParam("email") String email) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email);
    if (userList.size() > 0) {
      current = userList.get(0);
      current.addSavedEvents(eventName);
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }
  

  @PostMapping("delete-saved-event")
  public RedirectView deleteSavedEvent(
      @RequestParam("event-name") String eventName,
      @RequestParam("email") String email) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(email);
    if (userList.size() > 0) {
      current = userList.get(0);
      current.deleteSavedEvents(eventName);
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }
}