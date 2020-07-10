package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
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

  @Field(name="email")
  String email;

  @Field(name="university")
  String university;

  @Field(name="user-type")
  String userType;

  @Field(name="image")
  String image;

  @Field(name="saved-events")
  Set<Long> savedEvents;

  @Field(name="saved-organizations")
  Set<Long> savedOrganizations;

  public Individual() {
  }

  public Individual(Long timestamp, String firstName, String lastName, String email, String university, String userType, String image) {
    this.timestamp = timestamp;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.image = image;
    savedEvents = new HashSet<Long>();
    savedOrganizations = new HashSet<Long>();
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

  public String getImage() {
    return image;
  }

  public Set<Long> getSavedEvents() {
    return savedEvents;
  }

  public Set<Long> getSavedOrganizations() {
    return savedOrganizations;
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

  public void addSavedEvents(long event) {
    savedEvents.add(event);
  }

  public void deleteSavedEvents(long event) {
    savedEvents.remove(event);
  }

  /**
   * add a new organization to the list
   * @param organization organization to be added
   */
  public void addSavedOrganizations(long organization) {
    savedOrganizations.add(organization);
  }

  /**
   * deletes an organization from the list
   * @param organization organization to be deleted
   */
  public void deleteSavedOrganizations(long organization) {
    savedOrganizations.remove(organization);
  }
}
