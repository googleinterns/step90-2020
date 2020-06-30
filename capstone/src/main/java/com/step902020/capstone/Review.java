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

  LocalDateTime date;

  //User user;

  @Field(name="text")
  String text;

  //@Field(name="image-filename")
  //String imageFilename;

  public Review(LocalDateTime date, String text) {
    this.date = date;
    this.text = text;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getText() {
    return text;
  }

  //public String getImageFilename() {
  //  return imageFilename;
  //}
}