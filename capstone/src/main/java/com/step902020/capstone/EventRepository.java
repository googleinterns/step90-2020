package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.List;

/**
 * Spring Data Repository for Event Entities
 */
public interface EventRepository extends DatastoreRepository<Event, Long> {
  public long deleteAllByEventDateTime(String eventDateTime);
  public List<Event> findAllByUniversity(String university);
}
