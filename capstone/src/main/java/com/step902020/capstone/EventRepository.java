package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Spring Data Repository for Event Entities
 */
public interface EventRepository extends DatastoreRepository<Event, Long> {
  public long deleteByEventDateTime(String eventDateTime);
  public List<Event> findByUniversity(String university, Pageable pageable);
  public List<Event> findByUniversity(String university);
}
