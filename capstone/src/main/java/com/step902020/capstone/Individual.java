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

  String university;

  @Field(name="user-type")
  String userType;

  @Reference
  List<Event> savedEvents;

  @Reference
  List<Organization> organizations;

  public Individual() {
  }

  public Individual(Long timestamp, String firstName, String lastName, String email, String university, String userType) {
    this.timestamp = timestamp;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.university = university;
    this.userType = userType;
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

  public String getUniversity() {
    return university;
  }

  public String getUserType() {
    return userType;
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

  public void addSavedEvents(Event event) {
    savedEvents.add(event);
  }

  public void deleteSavedEvents(Event event) {
    savedEvents.remove(event);
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
    organizations.remove(organization);
  }

  /**
   * returns user information in a string format
   * @return String
   */
  public String toString() {
    return firstName + lastName + " " + datastoreId;
  }

  /**
   * implement equality
   * @param o object being compared to
   * @return boolean
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Individual)) {
      return false;
    }

    Individual individual = (Individual) o;
    return this.datastoreId.equals(individual.datastoreId);
  }

  @Override
  public int hashCode() {
    return datastoreId.hashCode();
  }
}