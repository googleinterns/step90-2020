package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

/**
 * 
 */
public interface EventRepository extends DatastoreRepository<EventTemp, Long> {
    //public EventTemp findById(long id);
}