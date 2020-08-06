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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = CapstoneApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationControllerDatastoreTests {
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
  @Autowired private ReviewRepository reviewRepository;
  @Autowired private TestRestTemplate restTemplate;
  private TestRestTemplate authRestTemplate;

  private Individual expectedIndividual;
  private Organization expectedOrganization;
  private Organization expectedOrganization2;
  private Organization expectedOrganization3;
  private Event expectedEvent;
  private University expectedUniversity;
  private Review expectedReview;

  @Before
  public void setUp() {
    // create a university for testing
    expectedUniversity = new University("Test", 40.769579, -73.973036);
    this.universityRepository.save(expectedUniversity);

    // create an individual using current user email credentials in datastore
    Individual individual = new Individual(System.currentTimeMillis(), "UserWithOrganization", "ThatExists",
            currentUserEmail, expectedUniversity, "individual");
    expectedIndividual = this.individualRepository.save(individual);

    // create an organization using the current user email in datastore
    expectedOrganization = this.organizationRepository.save(new Organization(System.currentTimeMillis(),
            "new organization", currentUserEmail, expectedUniversity, "organization",
            "hello world!", "arts"));
    expectedOrganization.incrementRank();
    this.organizationRepository.save(expectedOrganization);

    // create two more organizations for testing
    expectedOrganization2 = this.organizationRepository.save(new Organization(System.currentTimeMillis(),
            "new organization 2", "org2@uni.edu", expectedUniversity, "organization",
            "hello world!", "service"));
    expectedOrganization2.incrementRank();
    expectedOrganization2.incrementRank();
    this.organizationRepository.save(expectedOrganization2);

    expectedOrganization3 = this.organizationRepository.save(new Organization(System.currentTimeMillis(),
            "new organization 2", "org3@uni.edu", expectedUniversity, "organization",
            "hello world!", "arts"));
    expectedOrganization3.incrementRank();
    expectedOrganization3.incrementRank();
    expectedOrganization3.incrementRank();
    this.organizationRepository.save(expectedOrganization3);

    // create an event that belongs to the expectedOrganization
    expectedEvent = this.eventRepository.save(new Event(expectedUniversity, expectedOrganization.getDatastoreId(), "pizza party",
            "2020-06-01T12:30:00", "Turtles bring pizza",40.769579,
            -73.973036, "movie", "2", "indoors", true,
            false, true));
    expectedOrganization.addEvent(expectedEvent);

    this.organizationRepository.save(expectedOrganization);
    this.authRestTemplate = this.restTemplate.withBasicAuth(currentUserEmail, currentUserPassword);

    String individualName = individual.firstName + " " + individual.lastName;
    expectedReview = new Review(individualName, individual.email, "10/10 Test Expected Review");
  }

  @After
  public void tearDown() {
    this.individualRepository.deleteByEmail(currentUserEmail);
    this.organizationRepository.deleteByEmail(expectedOrganization.getEmail());
    this.organizationRepository.deleteByEmail(currentUserEmail);
    this.organizationRepository.deleteByEmail("org@uni.edu");
    this.organizationRepository.deleteByEmail("org1@uni.edu");
    this.organizationRepository.deleteByEmail("org2@uni.edu");
    this.organizationRepository.deleteByEmail("org3@uni.edu");
    this.eventRepository.deleteById(expectedEvent.getDatastoreId());
    this.universityRepository.deleteByName(expectedUniversity.getName());
    this.reviewRepository.deleteByIndividualEmail(expectedIndividual.getEmail());
  }

  @Test
  public void testGetOrganization() throws URISyntaxException {
    String expectedEmail = expectedOrganization.getEmail();
    final String baseUrl = "/get-organization";
    Organization result = authRestTemplate.getForObject(baseUrl, Organization.class);
    assertEquals("Wrong user returned", expectedEmail, result.getEmail());
  }

  @Test
  public void testSaveOrganization() throws URISyntaxException {
    String url = "/save-organization";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    String newName = "org name changed";
    String newDescription = "description changed";
    String newOrgType = "service";
    map.add("name", newName);
    map.add("user-type", expectedOrganization.getOrgType());
    map.add("university", expectedOrganization.getUniversity().getName());
    map.add("description", newDescription);
    map.add("org-type", newOrgType);
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);
    assertTrue(saveResponse.getHeaders().containsKey("Location"));
    String redirectLocation = saveResponse.getHeaders().getFirst("Location");
    assertTrue(redirectLocation.endsWith("profile.html"));

    // check whether it is saved correctly
    final String getOrganizationUrl = "/get-organization";

    Organization result = authRestTemplate.getForObject(getOrganizationUrl, Organization.class);
    assertEquals("Wrong organization name", newName, result.getName());
    assertEquals("Wrong organization description", newDescription, result.getDescription());
    assertEquals("Wrong organization type", newOrgType, result.getOrgType());
  }

  @Test
  public void testSearchOrganizationUniversityArgumentOnly() throws URISyntaxException {
    final String baseUrl = "/search-organization?name=&university=" + expectedUniversity.getName() + "&orgTypes=";
    List<Organization> result = Arrays.asList(authRestTemplate.getForObject(baseUrl, Organization[].class));
    assertEquals("Wrong number returned", 3, result.size());
    Organization[] expected = new Organization[]{expectedOrganization, expectedOrganization2, expectedOrganization3};
    for(int i = 0; i < expected.length; i++) {
      assertEquals("Organization " + expected[i].getName()  + " not included at position " + i, true, result.contains(expected[i]));
    }
  }

  @Test
  public void testSearchOrganizationUniversityArgumentAndOrgTypeArgument() throws URISyntaxException {
    final String baseUrl = "/search-organization?name=&university=" + expectedUniversity.getName() + "&orgTypes=arts";
    List<Organization> result = Arrays.asList(authRestTemplate.getForObject(baseUrl, Organization[].class));
    assertEquals("Wrong number returned", 2, result.size());
    Organization[] expected = new Organization[]{expectedOrganization, expectedOrganization3};
    for(int i = 0; i < expected.length; i++) {
      assertEquals("Organization " + expected[i].getName()  + " not included at position " + i, true, result.contains(expected[i]));
    }
  }

  @Test
  public void testSearchOrganizationUniversityArgumentAndOrgTypeArgumentAndNameArgument() throws URISyntaxException {
    final String baseUrl = "/search-organization?name=" + expectedOrganization2.getName() + "&university=" + expectedUniversity.getName() + "&orgTypes=arts";
    List<Organization> result = Arrays.asList(authRestTemplate.getForObject(baseUrl, Organization[].class));
    assertEquals("Wrong number returned", 2, result.size());
    Organization[] expected = new Organization[]{expectedOrganization2, expectedOrganization3};
    for(int i = 0; i < expected.length; i++) {
      assertEquals("Organization " + expected[i].getName()  + " not included at position " + i, true, result.contains(expected[i]));
    }
  }

  @Test
  public void testOrganizationCreateEvent() throws URISyntaxException {
    String url = "/save-event";
    String expectedEmail = expectedOrganization.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("eventTitle", "new event");
    map.add("eventDateTime", "2020-06-01T12:30:00");
    map.add("eventDescription", "hello");
    map.add("eventLatitude", "0");
    map.add("eventLongitude", "0");
    map.add("eventType", "arts");
    map.add("energyLevel", "2");
    map.add("location", "indoors");
    map.add("foodAvailable", true);
    map.add("free", true);
    map.add("visitorAllowed", true);
    map.add("event-id", "");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);
    // getting the actual result
    final String baseUrl = "/get-organization?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
    Set<Long> expectedSet = new HashSet<>();
    expectedSet.add(234L);
    Organization result = authRestTemplate.getForObject(uri, Organization.class);

    assertNotNull("Unexpected organization ID",result);
    assertEquals("Unexpected event count", 2, result.getEvents().size());
    assertEquals("Unexpected organization Name", expectedOrganization.getName(), result.getName());
    this.eventRepository.deleteByEventDateTime("2020-06-01T12:30:00");
  }

  @Test
  public void testOrganizationDeleteEvent() throws URISyntaxException {
    String url = "/delete-organization-event";
    String expectedEmail = expectedOrganization.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("event-id", expectedEvent.getDatastoreId());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);

    // Check organization after delete ("postcondition")
    Organization postResult = authRestTemplate.getForObject("/get-organization", Organization.class);
    assertEquals(
            "Delete event error", 0, postResult.getEvents().size());
  }

  @Test
  public void testGetOrganizationPublicProfile() throws URISyntaxException {
    String expectedEmail = expectedOrganization2.getEmail();
    final String baseUrl = "/get-public-profile?organization-id=" + expectedOrganization2.getDatastoreId();
    Organization result = authRestTemplate.getForObject(baseUrl, Organization.class);
    assertEquals("Wrong user returned", expectedEmail, result.getEmail());
  }

  @Test
  public void testAddReview() throws URISyntaxException {
    String url = "/add-org-review";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("reviewedObjectId", expectedOrganization.getDatastoreId());
    map.add("text", expectedReview.getText());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);

    // getting the actual result
    final String baseUrl = "/get-public-profile?organization-id=" + expectedOrganization.getDatastoreId();
    URI uri = new URI(baseUrl);
    Organization result = authRestTemplate.getForObject(uri, Organization.class);
    assertEquals("Wrong number of reviews",  1, result.getReviews().size());
    assertTrue("Wrong review -- text", expectedReview.text.equals(result.getReviews().get(0).text));
  }

  @Test
  public void testRemoveReview() throws URISyntaxException {
    // first add a review
    expectedOrganization.addReview(expectedReview);
    this.organizationRepository.save(expectedOrganization);
    this.reviewRepository.save(expectedReview);
    // remove review
    String url = "/remove-org-review";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("reviewedObjectId", expectedOrganization.getDatastoreId());
    map.add("reviewId", expectedReview.getDatastoreId());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);

    // getting the actual result
    final String baseUrl = "/get-public-profile?organization-id=" + expectedOrganization.getDatastoreId();
    URI uri = new URI(baseUrl);
    Organization result = authRestTemplate.getForObject(uri, Organization.class);
    assertEquals("Wrong number of reviews",  0, result.getReviews().size());
  }
}
