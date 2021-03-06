package com.step902020.capstone;

import java.util.List;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepository extends DatastoreRepository<Organization, Long> {

  public Organization findFirstByEmail(String email);

  public long deleteByEmail(String email);

  public List<Organization> findByEmail(List<String> email);

  public List<Organization> findByUniversityOrderByRankDesc(University university);

  public List<Organization> findByUniversityAndOrgType(University university, String orgType);

  public List<Organization> findByUniversity(University university, Pageable pageable);

  @Query("select * from organization where name >= @name and name < @endname and university = @university order by name asc, rank desc")
  public List<Organization> findOrganizationsByNameMatchingOrderByRankDesc(@Param("name") String name, @Param("endname") String endname, @Param("university") University university);
}
