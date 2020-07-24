package com.step902020.capstone;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepository extends DatastoreRepository<Organization, Long> {

  public Organization findFirstByEmail(String email);

  public long deleteByEmail(String email);

  public List<Organization> findByEmail(List<String> email);

  public List<Organization> findByUniversity(String university);

  public List<Organization> findByUniversity(String university, Pageable pageable);

  @Query("select * from organization where name >= @name and name < @endname and university = @university")
  public List<Organization> findOrganizationsByNameMatching(@Param("name") String name, @Param("endname") String endname, @Param("university") String university);
}
