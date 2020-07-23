package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import java.util.*;

@Entity(name = "individual")
public class Individual {
  @Id
  Long datastoreId;

  Long timestamp;

  @Field(name="firstname")
  String firstName;

  @Field(name="lastname")
  String lastName;

  String email;

  @Reference
  University university;

  @Field(name="user-type")
  String userType;

  String image;

  @Reference
  List<Event> savedEvents;

  @Reference
  List<Organization> organizations;

  public Individual() {
  }

  public Individual(Long timestamp, String firstName, String lastName, String email, University university, String userType, String image) {
    this.timestamp = timestamp;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.image = image;
    savedEvents = new ArrayList<Event>();
    organizations = new ArrayList<Organization>();
  }
  
  public Long getDatastoreId() {
    return datastoreId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public University getUniversity() {
    return university;
  }

  public String getUserType() {
    return userType;
  }

  public String getImage() {
    return image;
  }

  public List<Event> getSavedEvents() {
    return savedEvents;
  }

  public List<Organization> getOrganizations() {
    return organizations;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void addSavedEvents(Event event) {
    savedEvents.add(event);
  }

  public void deleteSavedEvents(Event event) {
    savedEvents.removeIf(e -> event.getDatastoreId().equals(e.getDatastoreId()));
  }

  /**
   * add a new organization to the list
   * @param organization organization to be added
   */
  public void addOrganizations(Organization organization) {
    organizations.add(organization);
  }

  /**
   * deletes an organization from the list
   * @param organization organization to be deleted
   */
  public void deleteOrganizations(Organization organization) {
    organizations.removeIf(o -> organization.getDatastoreId().equals(o.getDatastoreId()));
  }
}