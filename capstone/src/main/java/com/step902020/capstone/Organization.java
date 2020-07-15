package com.step902020.capstone;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import java.util.*;

@Entity(name = "organization")
public class Organization {
  @Id
  Long datastoreId;

  Long timestamp;

  String name;

  String email;

  String university;

  @Field(name="user-type")
  String userType;
  String description;
  String image;

  @Reference
  List<Event> events;
  
  public Organization() {
  }
  
  public Organization(Long timestamp, String name, String email, String university, String userType, String description, String image) {
    this.timestamp = timestamp;
    this.name = name;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.description = description;
    this.image = image;
    events = new ArrayList<Event>();
  }
  
  public Long getDatastoreId() {
    return datastoreId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public String getName() {
    return name;
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

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }
  
  public List<Event> getEvents() {
    return events;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  public void deleteEvent(Event event) {
    events.removeIf(e -> event.getDatastoreID().equals(e.getDatastoreID()));
  }
  
}
