package com.step902020.capstone;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface UniversityRepository extends DatastoreRepository<University, Long> {

  public University findFirstByName(String name);
  public Long deleteByName(String name);
}
