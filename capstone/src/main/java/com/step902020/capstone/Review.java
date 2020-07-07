package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

/**
 * 
 */
@Entity(name = "review")
public class Review {

  long timestamp;

  LocalDateTime date;

  @Field(name="name")
  String name;

  @Field(name="text")
  String text;

  //@Field(name="image-filename")
  //String imageFilename;

  public Review(String text, String name) {
    this.date = LocalDateTime.now();
    this.timestamp = System.currentTimeMillis();
    this.text = text;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getText() {
    return text;
  }

 // public String getImageFilename() {
  //  return imageFilename;
  //}
}