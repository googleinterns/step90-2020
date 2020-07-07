package com.step902020.capstone;

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
  String organizationName;

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

    public Event(String organizationName, String eventTitle,String eventDateTime, String eventDescription, double eventLatitude, double eventLongitude) {
 
    this.organizationName = organizationName;  
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
  public String getOrganizationName() {
    return organizationName;
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

}
