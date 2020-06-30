package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;

@Entity(name = "eventTemp")
public class EventTemp {

  @Id
  Long datastoreId;

  String text;

  public EventTemp(String text) {
    this.text = text;
  }

  public Long getDatastoreId() {
    return datastoreId;
  }

  public String getText() {
    return text;
  }
}