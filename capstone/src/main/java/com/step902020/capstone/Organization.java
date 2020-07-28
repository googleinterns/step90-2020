package com.step902020.capstone;

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

  @Reference
  University university;

  @Field(name="user-type")
  String userType;
  String description;

  @Reference
  List<Event> events;

  Integer rank;
  public Organization() {
  }

  public Organization(Long timestamp, String name, String email, University university, String userType, String description) {
    this.timestamp = timestamp;
    this.name = name;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.description = description;
    events = new ArrayList<Event>();
    rank = 0;
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

  public University getUniversity() {
    return university;
  }

  public String getUserType() {
    return userType;
  }

  public String getDescription() {
    return description;
  }
  
  public List<Event> getEvents() {
    return events;
  }

  public Integer getRank() {
    return rank;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  /**
   * increase rank by 1
   */
  public void addRank() {
    rank++;
  }

  /**
   * decrease rank by 1
   */
  public void minusRank() {
    rank--;
  }

  public void deleteEvent(Event event) {
    events.remove(event);
  }

  /**
   * returns organization information in a string format
   * @return String
   */
  public String toString() {
    return name + " " + datastoreId;
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
    if (!(o instanceof Organization)) {
      return false;
    }

    Organization organization = (Organization) o;
    return this.datastoreId.equals(organization.datastoreId);
  }

  @Override
  public int hashCode() {
    return datastoreId.hashCode();
  }
  
}
