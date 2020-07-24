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
public class UserDatastoreTest {

  /* Properties for Test account from test/resources/application-local.properties */
  @Value("${spring.security.user.name}")
  private String currentUserEmail;

  @Value("${spring.security.user.password}")
  private String currentUserPassword;

  @Autowired private IndividualRepository individualRepository;
  @Autowired private OrganizationRepository organizationRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private TestRestTemplate restTemplate;
  private TestRestTemplate authRestTemplate;

  Individual expectedIndividual;
  Organization expectedOrganization;
  Event expectedEvent;
  Organization organizationSavedByUser;

  @Before
  public void setUp() {
    // append a random number to email to make a new user

    Organization organization = new Organization(System.currentTimeMillis(), "OrganizationThatExists",
        "org@uni.edu", "UNI", "organization",
        "Organization already saved by a user");
    this.organizationSavedByUser = this.organizationRepository.save(organization);
    Individual individual = new Individual(
        System.currentTimeMillis(),
        "UserWithOrganization",
        "ThatExists",
        currentUserEmail,
        "Princeton",
        "individual");
    individual.addOrganizations(this.organizationSavedByUser);

    expectedIndividual =
        this.individualRepository.save(individual);
    expectedOrganization =
        this.organizationRepository.save(
            new Organization(
                System.currentTimeMillis(),
                "new organization",
                currentUserEmail,
                "Princeton",
                "organization",
                "hello world!"));

    expectedEvent = this.eventRepository.save(new Event("Princeton", expectedOrganization.getName(), expectedOrganization.getDatastoreId(), "pizza party", "2020-06-01T12:30:00EST", "Turtles bring pizza",
        40.769579, -73.973036, true, false));
    expectedOrganization.addEvent(expectedEvent);
    this.organizationRepository.save(expectedOrganization);

    this.authRestTemplate = this.restTemplate
        .withBasicAuth(currentUserEmail, currentUserPassword);
  }

  @After
  public void tearDown() {
    this.individualRepository.deleteByEmail(expectedIndividual.getEmail());
    this.individualRepository.deleteByEmail(currentUserEmail);
    this.organizationRepository.deleteByEmail(expectedOrganization.getEmail());
    this.organizationRepository.deleteByEmail(currentUserEmail);
    this.eventRepository.deleteAllByEventDateTime(expectedEvent.getEventDateTime());
  }

  @Test
  public void testGetIndividual() throws URISyntaxException {
    // getting the actual result
    Individual result = authRestTemplate.getForObject("/get-individual", Individual.class);
    assertEquals("Wrong user returned", currentUserEmail, result.getEmail());
  }

  @Test
  public void testGetOrganization() throws URISyntaxException {
    String expectedEmail = expectedOrganization.getEmail();
    final String baseUrl = "/get-organization";
    Organization result = authRestTemplate.getForObject(baseUrl, Organization.class);
    assertEquals("Wrong user returned", expectedEmail, result.getEmail());
  }

  @Test
  public void testUserAddSavedEvent() throws URISyntaxException {
    String url = "/add-saved-event";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("event-id", expectedEvent.getDatastoreId());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);
    assertTrue(saveResponse.getHeaders().containsKey("Location"));
    String redirectLocation = saveResponse.getHeaders().getFirst("Location");
    assertTrue(redirectLocation.endsWith("savedevents.html"));

    // getting the actual result
    final String getIndividualUrl = "/get-individual";

    Individual result = authRestTemplate.getForObject(getIndividualUrl, Individual.class);
    assertEquals("Wrong number of saved events", 1, result.getSavedEvents().size());
    assertEquals("Wrong saved event -- ID", expectedEvent.getDatastoreId(), result.getSavedEvents().get(0).getDatastoreId());
    assertEquals("Wrong saved event -- title", expectedEvent.getEventTitle(), result.getSavedEvents().get(0).getEventTitle());
  }

  @Test
  public void testUserDeleteSavedEvent() throws URISyntaxException {
    String url = "/delete-saved-event";
    String expectedEmail = expectedIndividual.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("email", expectedEmail);
    map.add("event-id", expectedEvent.getDatastoreId());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);
    // getting the actual result
    final String baseUrl = "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
    Individual result = authRestTemplate.getForObject(uri, Individual.class);
    assertEquals("Delete event error", 0, result.getSavedEvents().size());
  }

  @Test
  public void testAddSavedOrganization() throws URISyntaxException {
    String url = "/add-saved-organization";
    String expectedEmail = expectedIndividual.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("email", expectedEmail);
    map.add("organization-id", expectedOrganization.getDatastoreId());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);
    // getting the actual result
    final String baseUrl = "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
    Set<Long> expectedSet = new HashSet<>();
    expectedSet.add(234L);
    Individual result = authRestTemplate.getForObject(uri, Individual.class);
    assertEquals("Unexpected organization count", 2, result.getOrganizations().size());
    Optional<Organization> newOrg = result.getOrganizations().stream()
        .filter(o -> o.getDatastoreId().equals(expectedOrganization.getDatastoreId()))
        .findAny();
    assertFalse("Unexpected organization ID", newOrg.isEmpty());
    assertEquals("Unexpected organization Name", expectedOrganization.getName(), newOrg.get().getName());
  }

  @Test
  public void testDeleteSavedOrganization() throws URISyntaxException {

    // Check saved organizations before delete ("precondition")
    Individual preResult = authRestTemplate.getForObject("/get-individual", Individual.class);
    assertEquals(
        "Unexpected number of organizations before the test",
        1, preResult.getOrganizations().size());

    // Exercise functionality
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("organization-id", organizationSavedByUser.getDatastoreId());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity("/delete-saved-organization", request, String.class);

    // Check saved organizations after delete ("postcondition")
    Individual postResult = authRestTemplate.getForObject("/get-individual", Individual.class);
    assertEquals(
        "Delete organization error", 0, postResult.getOrganizations().size());
  }

  @Test
  public void testOrganizationCreateEvent() throws URISyntaxException {
    String url = "/save-event";
    String expectedEmail = expectedIndividual.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("eventTitle", "new event");
    map.add("eventDateTime", "2020-06-01T12:30:00EST");
    map.add("eventDescription", "hello");
    map.add("eventLatitude", "0");
    map.add("eventLongitude", "0");
    map.add("foodAvailable", true);
    map.add("requiredFee", true);
    map.add("event-id", "");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);
    // getting the actual result
    final String baseUrl = "/get-organization?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
    Set<Long> expectedSet = new HashSet<>();
    expectedSet.add(234L);
    Organization result = authRestTemplate.getForObject(uri, Organization.class);

    assertFalse("Unexpected organization ID",result == null);
    assertEquals("Unexpected event count", 2, result.getEvents().size());
    assertEquals("Unexpected organization Name", expectedOrganization.getName(), result.getName());
  }

  @Test
  public void testOrganizationDeleteEvent() throws URISyntaxException {

    String url = "/delete-organization-event";
    String expectedEmail = expectedIndividual.getEmail();
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
}
