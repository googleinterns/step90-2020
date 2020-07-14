package com.step902020.capstone;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface IndividualRepository extends DatastoreRepository<Individual, Long> {

  public Optional<Individual> findByEmail(String email);

  public long deleteByEmail(String email);
}
