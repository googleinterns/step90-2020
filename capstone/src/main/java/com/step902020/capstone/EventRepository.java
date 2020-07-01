package com.step902020.capstone;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.core.convert.converter.Converter;

/**
 * 
 */
public interface EventRepository extends DatastoreRepository<EventTemp, Long> {
    public EventTemp findById(long id);
}