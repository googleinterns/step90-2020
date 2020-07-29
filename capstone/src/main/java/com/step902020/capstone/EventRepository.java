package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Spring Data Repository for Event Entities
 */
public interface EventRepository extends DatastoreRepository<Event, Long> {
  // used to delete test data only
  public Long deleteByEventDateTime(String eventDateTime);

  public List<Event> findByUniversityOrderByRankDesc(University university, Pageable pageable);
  public List<Event> findByUniversityOrderByRankDesc(University university);
}
