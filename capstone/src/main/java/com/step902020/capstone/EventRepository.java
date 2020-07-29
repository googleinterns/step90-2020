package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data Repository for Event Entities
 */
public interface EventRepository extends DatastoreRepository<Event, Long> {
  // used to delete test data only
  public Long deleteByEventDateTime(String eventDateTime);

  public List<Event> findByUniversity(University university, Pageable pageable);
  public List<Event> findByUniversity(University university);
  public List<Event> findByUniversityAndEventDateTimeGreaterThan(University university, String date);
  public List<Event> findByUniversityAndEventDateTimeGreaterThan(University university, String date, Pageable pageable);
}
