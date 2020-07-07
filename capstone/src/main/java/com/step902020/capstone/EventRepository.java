package com.step902020.capstone;

import java.util.List;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface EventRepository extends DatastoreRepository<Event, Long> {

  public List<Event> findByDatastoreID(Long datastoreID);

}