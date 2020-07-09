package com.step902020.capstone;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Id;
import java.util.*;

@Entity(name = "event")
public class Event{

  @Id
  Long datastoreID;

  @Reference
  @JsonBackReference
  Organization organization;

  @Field(name="eventTitle")
  String eventTitle;

  @Field(name="eventDateTime")
  String eventDateTime;

  @Field(name="eventDescription")
  String eventDescription;

  @Field(name="eventLatitude")
  double eventLatitude;

  @Field(name="eventLongitude")
  double eventLongitude;
 

  public Event(Organization organization, String eventTitle,String eventDateTime, String eventDescription, double eventLatitude, double eventLongitude) {
    
    this.organization = organization;
    this.eventTitle = eventTitle;
    this.eventDateTime = eventDateTime;
    this.eventDescription = eventDescription;
    this.eventLatitude = eventLatitude;
    this.eventLongitude = eventLongitude;
  }

  public Long getDatastoreID() {
    return datastoreID;
  }

  public String getEventTitle() {
    return eventTitle;
  }

  public Organization getOrganization() {
    return organization;
  }
  public String getEventDateTime() {
    return eventDateTime;
  }
  public String getDescription() {
    return eventDescription;
  }
  public double getEventLatitude() {
    return eventLatitude;
  }
  public double getEventLongitude() {
    return eventLongitude;
  }

  public void setDatastoreID(Long datastoreID) {
    this.datastoreID = datastoreID;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public void setEventTitle(String eventTitle) {
    this.eventTitle = eventTitle;
  }

  public void setEventDateTime(String eventDateTime) {
    this.eventDateTime = eventDateTime;
  }

  public void setEventDescription(String eventDescription) {
    this.eventDescription = eventDescription;
  }

  public void setEventLatitude(double eventLatitude) {
    this.eventLatitude = eventLatitude;
  }

  public void setEventLongitude(double eventLongitude) {
    this.eventLongitude = eventLongitude;
  }

}
