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
  Set<String> savedEvents;

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
    savedEvents = new HashSet<String>();
    savedEvents.add("hello 1");
    savedEvents.add("hello 2");
    savedEvents.add("hello 3");
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

  public Set<String> getSavedEvents() {
    return savedEvents;
  }

  public void editFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void editLastName(String lastName) {
    this.lastName = lastName;
  }

  public void editUniversity(String university) {
    this.university = university;
  }

  public void editImage(String image) {
    this.image = image;
  }

  public void addSavedEvents(String event) {
    savedEvents.add(event);
  }

  public void deleteSavedEvents(String event) {
    savedEvents.remove(event);
  }
}