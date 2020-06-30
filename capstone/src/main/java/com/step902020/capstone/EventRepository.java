package com.step902020.capstone;
import java.util.List;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface EventRepository extends DatastoreRepository<EventTemp, Long> {

}