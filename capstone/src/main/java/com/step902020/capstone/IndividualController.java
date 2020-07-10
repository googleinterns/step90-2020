
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import java.io.IOException;


@RestController
public class IndividualController {

  @Autowired
  private IndividualRepository individualRepository;

  /** Find currently logged in individual. */
  @GetMapping("get-individual")
  public List<Individual> getIndividual(CurrentUser user) {
    return this.individualRepository.findByEmail(user.getEmail());
  }

  /** Save user information into Datastore. If the email does not yet exist in 
  Datastore, create a new entity. Otherwise do an update on the existing entity */
  @PostMapping("save-individual")
  public RedirectView saveIndividual(
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      CurrentUser user,
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university) throws IOException {

    String userEmail = user.getEmail();
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(userEmail);
    
    // either edit the existing user or create a new one
    if (userList.size() > 0) {
      current = userList.get(0);
      current.setFirstName(firstname);
      current.setLastName(lastname);
    }
    else {
        current = new Individual(System.currentTimeMillis(), firstname, lastname, userEmail, university, userType, "");
    }
    this.individualRepository.save(current);
    return new RedirectView("profile.html", true);
  }

  /** Add the event with the event id to the current individual's list of saved events */
  @PostMapping("add-saved-event")
  public RedirectView addSavedEvent(
      @RequestParam("event-id") String eventId,
      CurrentUser user) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    if (userList.size() > 0) {
      current = userList.get(0);
      current.addSavedEvents(Long.parseLong(eventId));
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }
  
  /** delete the event with the event id from the current individual's list of saved events */
  @PostMapping("delete-saved-event")
  public RedirectView deleteSavedEvent(
      @RequestParam("event-id") String eventId,
      CurrentUser user) throws IOException {

    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    if (userList.size() > 0) {
      current = userList.get(0);
      current.deleteSavedEvents(Long.parseLong(eventId));
    }
    this.individualRepository.save(current);
    return new RedirectView("savedevents.html", true);
  }

  /** Add the organization with the organization id to the current 
  individual's list of saved organizations */
  @PostMapping("add-saved-organization")
  public RedirectView addSavedOrganizations(
      CurrentUser user,
      @RequestParam("organization-id") String organizationId) throws IOException {
    
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    if (userList.size() > 0) {
      current = userList.get(0);
      current.addSavedOrganizations(Long.parseLong(organizationId));
    } 
    this.individualRepository.save(current);
    return new RedirectView("organizationsearch.html", true);
  }

   /** delete the organization with the organization id from the current 
  individual's list of saved organizations */
  @PostMapping("delete-saved-organization")
  public RedirectView deleteSavedOrganization(
      CurrentUser user,
      @RequestParam("organization-id") String organizationId) throws IOException {
    
    Individual current = null;
    List<Individual> userList = this.individualRepository.findByEmail(user.getEmail());
    if (userList.size() > 0) {
      current = userList.get(0);
      current.deleteSavedOrganizations(Long.parseLong(organizationId));
    } 
    this.individualRepository.save(current);
    return new RedirectView("savedorganizations.html", true);
  }
}