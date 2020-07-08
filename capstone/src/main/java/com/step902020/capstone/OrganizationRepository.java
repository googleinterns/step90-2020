package com.step902020.capstone;

import java.util.List;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface OrganizationRepository extends DatastoreRepository<Organization, Long> {

  public List<Organization> findByEmail(String email);

  public long deleteByEmail(String email);

  public List<Organization> findAllByEmail(List<String> email);
}
