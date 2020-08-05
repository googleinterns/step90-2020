package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Spring Data Repository for Event Entities
 */
public interface EventRepository extends DatastoreRepository<Event, Long> {
  public Long deleteByEventDateTime(String eventDateTime);
  public List<Event> findByUniversityAndEventDateTimeGreaterThan(University university, String date);
  public List<Event> findByUniversityAndEventDateTimeGreaterThanOrderByEventDateTimeAscRankDesc(University university, String date);

  @Query("select * from event where eventTitle >= @eventTitle and eventTitle < @endname and university = @university")
  public List<Event> findEventsByNameMatching(@Param("eventTitle") String eventTitle, @Param("endname") String endname, @Param("university") University university);

}
