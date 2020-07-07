package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.*;

@Entity(name = "event")
public class Event{

  @Id
  Long datastoreID;

  @Field(name="organizationName")
  String organizationName;

  @Field(name="eventTitle")
  String eventTitle;

  @Field(name="eventDateTime")
  String eventDateTime;

  @Field(name="eventDescription")
  String eventDescription;

  // @Field(name="university")
  // String university;

  @Field(name="eventLatitude")
  double eventLatitude;

  @Field(name="eventLongitude")
  double eventLongitude;

  // @Field(name="eventFilters")
  // List<String> eventFilters;
  
  // public Event(Long datastoreID, Long organizationID, String organizationName, String eventTitle, LocalDateTime eventDateTime, String university, String eventDescription, Long eventLatitude, Long eventLongitude, List<String> eventFilters) {
    
  //   this.datastoreID = datastoreID;
  //   this.organizationID = organizationID;
  //   this.organizationName = organizationName;
  //   this.eventTitle = eventTitle;
  //   this.eventDateTime = eventDateTime;
  //   this.eventDescription = eventDescription;
  //   this.university = university;
  //   this.eventLatitude = eventLatitude;
  //   this.eventLongitude = eventLongitude;
  //   this.eventFilters = eventFilters;
  // }

    public Event(String organizationName, String eventTitle,String eventDateTime, String eventDescription, double eventLatitude, double eventLongitude) {
    
    this.organizationName = organizationName;
    this.eventTitle = eventTitle;
    this.eventDateTime = eventDateTime;
    //this.university = university;
    this.eventDescription = eventDescription;
    this.eventLatitude = eventLatitude;
    this.eventLongitude = eventLongitude;
    //this.eventFilters = eventFilters;

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
  public String geteventDateTime() {
    return eventDateTime;
  }
  public String getDescription() {
    return eventDescription;
  }
  // public String getuniversity() {
  //   return university;
  // }
  public double getEventLatitude() {
    return eventLatitude;
  }
  public double geteventLongitude() {
    return eventLongitude;
  }
  // public List<String> getEventFilters() {
  //   return eventFilters;
  // }

}
