package com.step902020.capstone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = CapstoneApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IdentityControllerDatastoreTests {
  /* Properties for Test account from test/resources/application-local.properties */
  @Value("${spring.security.user.name}")
  private String currentUserEmail;

  @Value("${spring.security.user.password}")
  private String currentUserPassword;

  @Autowired
  private ApplicationContext context;
  @Autowired private IndividualRepository individualRepository;
  @Autowired private OrganizationRepository organizationRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private UniversityRepository universityRepository;
  @Autowired private TestRestTemplate restTemplate;
  private TestRestTemplate authRestTemplate;
  private University expectedUniversity;
  @Before
  public void setUp() {
    // create a university for testing
    expectedUniversity = new University("Test", 40.769579, -73.973036);
    this.universityRepository.save(expectedUniversity);
  }

//  @After
//  public void tearDown() {
//    this.organizationRepository.deleteByEmail(currentUserEmail);
//    this.individualRepository.deleteByEmail(currentUserEmail);
//  }

  @Test
  public void testUserTypeOrganization() throws URISyntaxException {
    Organization expectedOrganization = this.organizationRepository.save(new Organization(System.currentTimeMillis(),
            "new organization", currentUserEmail, expectedUniversity, "organization",
            "hello world!", "arts"));

    Object postResult = authRestTemplate.getForObject("user-info", Organization.class);
    assertTrue("User type error", postResult instanceof Organization);
    assertEquals("Wrong organization returned", expectedOrganization, postResult);
  }

  @Test
  public void testUserTypeIndividual() throws URISyntaxException {
    Individual expectedIndividual = this.individualRepository.save(new Individual(System.currentTimeMillis(),
            "new individual", "ThatExists", currentUserEmail, expectedUniversity, "individual"));

    Object postResult = authRestTemplate.getForObject("user-info", Individual.class);
    assertTrue("User type error", postResult instanceof Individual);
    assertEquals("Wrong individual returned", expectedIndividual, postResult);
  }

  @Test
  public void testUserTypeUnknown() throws URISyntaxException {
    Object postResult = authRestTemplate.getForObject("user-info", Map.class);
    assertTrue("User type error", postResult instanceof Map);
    assertEquals("Wrong unknown individual returned", "unknown", ((Map<String, String>) postResult).get("userType"));
  }
}
