package com.step902020.capstone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = CapstoneApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventDatastoreTest {
  /* Properties for Test account from test/resources/application-local.properties */
  @Value("${spring.security.user.name}")
  private String currentUserEmail;

  @Value("${spring.security.user.password}")
  private String currentUserPassword;

  @Autowired private IndividualRepository individualRepository;
  @Autowired private OrganizationRepository organizationRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private UniversityRepository universityRepository;
  @Autowired private ReviewRepository reviewRepository;
  @Autowired private TestRestTemplate restTemplate;
  private TestRestTemplate authRestTemplate;

  Individual expectedIndividual;
  Organization expectedOrganization;
  Event expectedEvent;
  Review expectedReview;
  Review reviewAddedToEvent;
  University expectedUniversity;

  @Before
  public void setUp() {
    // append a random number to email to make a new user
    expectedUniversity = new University("Test", 40.769579, -73.973036);
    this.universityRepository.save(expectedUniversity);

    Individual individual = new Individual(System.currentTimeMillis(), "UserWithOrganization",
        "ThatExists", currentUserEmail, expectedUniversity,"individual");
    expectedIndividual = this.individualRepository.save(individual);

    expectedOrganization = this.organizationRepository.save(
            new Organization(System.currentTimeMillis(),"new organization", currentUserEmail,
                expectedUniversity, "organization", "hello world!"));

    expectedEvent = new Event(expectedUniversity, expectedOrganization.getName(),
            expectedOrganization.getDatastoreId(), "pizza party", "2020-06-01T12:30:00EST",
            "Turtles bring pizza", 40.769579, -73.973036, "movie",
            true, false);

    String individualName = individual.firstName + " " + individual.lastName;
    reviewAddedToEvent = new Review(individualName, individual.email, "10/10 Test Review Added to Event");
    expectedEvent.addReview(reviewAddedToEvent);
    this.eventRepository.save(expectedEvent);

    expectedReview = new Review(individualName, individual.email, "10/10 Test Expected Review");

    this.authRestTemplate = this.restTemplate
        .withBasicAuth(currentUserEmail, currentUserPassword);
  }

  @After
  public void tearDown() {
    this.individualRepository.deleteByEmail(expectedIndividual.getEmail());
    this.individualRepository.deleteByEmail(currentUserEmail);
    this.organizationRepository.deleteByEmail(expectedOrganization.getEmail());
    this.organizationRepository.deleteByEmail(currentUserEmail);
    this.reviewRepository.deleteByIndividualEmail(expectedIndividual.getEmail());
    this.universityRepository.deleteByName("Test");
    this.eventRepository.deleteByEventDateTime(expectedEvent.getEventDateTime());
  }

  @Test
  public void testGetEvent() throws URISyntaxException {
    // getting the actual result
    final String baseUrl = "/get-event?event-id=" + expectedEvent.datastoreId;
    Event result = authRestTemplate.getForObject(baseUrl, Event.class);
    assertEquals("Wrong event returned -- id", expectedEvent.datastoreId, result.datastoreId);
  }

  @Test
  public void testGetReview() throws URISyntaxException {
    // getting the actual result
    final String baseUrl = "/get-event?event-id=" + expectedEvent.datastoreId;
    URI uri = new URI(baseUrl);
    Event result = authRestTemplate.getForObject(uri, Event.class);
    assertEquals("Wrong number of reviews",  1, result.reviews.size());
    assertEquals("Wrong review -- id", expectedEvent.reviews.get(0).datastoreId, result.reviews.get(0).datastoreId);
  }

  @Test
  public void testAddReview() throws URISyntaxException {
    String url = "/new-review";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("eventId", expectedEvent.datastoreId);
    map.add("text", expectedReview.text);
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);

    // getting the actual result
    final String baseUrl = "/get-event?event-id=" + expectedEvent.datastoreId;
    URI uri = new URI(baseUrl);
    Event result = authRestTemplate.getForObject(uri, Event.class);
    assertEquals("Wrong number of reviews",  2, result.reviews.size());
    assertTrue("Wrong review -- text", expectedReview.text.equals(result.reviews.get(0).text) ||
            expectedReview.text.equals(result.reviews.get(1).text));
  }
}
