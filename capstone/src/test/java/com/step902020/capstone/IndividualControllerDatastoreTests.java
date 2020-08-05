package com.step902020.capstone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
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
public class IndividualControllerDatastoreTests {
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

  Individual expectedIndividual;
  Organization expectedOrganization;
  Organization expectedOrganization2;
  Organization expectedOrganization3;
  Event expectedEvent;
  Event expectedEvent2;
  Event expectedEvent3;
  Organization organizationSavedByUser;
  University expectedUniversity;

  @Before
  public void setUp() {
    // create a university for testing
    expectedUniversity = new University("Test", 40.769579, -73.973036);
    this.universityRepository.save(expectedUniversity);

    // create an organization that is saved by a user
    Organization organization = new Organization(System.currentTimeMillis(), "OrganizationThatExists",
        "org@uni.edu", expectedUniversity, "organization",
        "Organization already saved by a user", "arts");
    this.organizationSavedByUser = this.organizationRepository.save(organization);

    // create an individual using current user email credentials in datastore
    Individual individual = new Individual(System.currentTimeMillis(), "UserWithOrganization", "ThatExists",
        currentUserEmail, expectedUniversity, "individual");
    individual.addOrganizations(this.organizationSavedByUser);
    expectedIndividual = this.individualRepository.save(individual);

    // create three organizations in datastore
    expectedOrganization = this.organizationRepository.save(new Organization(System.currentTimeMillis(),
            "new organization", "org1@uni.edu", expectedUniversity, "organization",
                "hello world!", "arts"));
    expectedOrganization.incrementRank();
    this.organizationRepository.save(expectedOrganization);

    expectedOrganization2 = this.organizationRepository.save(new Organization(System.currentTimeMillis(),
            "new organization 2", "org2@uni.edu", expectedUniversity, "organization",
            "hello world!", "arts"));
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

    // create a past event
    LocalDateTime today =  LocalDateTime.now();
    LocalDateTime tomorrow = today.plusDays(1);
    expectedEvent = this.eventRepository.save(new Event(expectedUniversity, expectedOrganization.getDatastoreId(), "pizza party",
            "2020-06-01T12:30:00", "Turtles bring pizza",40.769579,
            -73.973036, "movie", "2", "indoors", true,
            false, true));
    this.eventRepository.save(expectedEvent);
    expectedOrganization.addEvent(expectedEvent);

    // create two future events
    expectedEvent2 = this.eventRepository.save(new Event(expectedUniversity, expectedOrganization.getDatastoreId(), "pizza party 2",
            tomorrow.toString(), "Turtles bring pizza",40.769579,
            -73.973036, "movie", "2", "indoors", true,
            false, true));
    expectedEvent2.incrementRank();
    this.eventRepository.save(expectedEvent2);
    expectedOrganization.addEvent(expectedEvent2);

    expectedEvent3 = this.eventRepository.save(new Event(expectedUniversity, expectedOrganization.getDatastoreId(), "pizza party 3",
            tomorrow.toString(), "Turtles bring pizza",40.769579,
            -73.973036, "movie", "2", "indoors", true,
            false, true));
    expectedEvent3.incrementRank();
    expectedEvent3.incrementRank();
    this.eventRepository.save(expectedEvent3);
    expectedOrganization.addEvent(expectedEvent3);

    this.organizationRepository.save(expectedOrganization);
    this.authRestTemplate = this.restTemplate.withBasicAuth(currentUserEmail, currentUserPassword);
  }

  @After
  public void tearDown() {
    this.individualRepository.deleteByEmail(expectedIndividual.getEmail());
    this.individualRepository.deleteByEmail(currentUserEmail);
    this.organizationRepository.deleteByEmail(expectedOrganization.getEmail());
    this.organizationRepository.deleteByEmail(currentUserEmail);
    this.organizationRepository.deleteByEmail("org@uni.edu");
    this.organizationRepository.deleteByEmail("org1@uni.edu");
    this.organizationRepository.deleteByEmail("org2@uni.edu");
    this.organizationRepository.deleteByEmail("org3@uni.edu");
    this.eventRepository.deleteById(expectedEvent.getDatastoreId());
    this.eventRepository.deleteById(expectedEvent2.getDatastoreId());
    this.eventRepository.deleteById(expectedEvent3.getDatastoreId());
    this.universityRepository.deleteByName(expectedUniversity.getName());
  }

  @Test
  public void testGetIndividual() throws URISyntaxException {
    // getting the actual result
    Individual result = authRestTemplate.getForObject("/get-individual", Individual.class);
    assertEquals("Wrong user returned", currentUserEmail, result.getEmail());
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
    assertEquals("Wrong saved event -- ID", expectedEvent.getDatastoreId(), result.getSavedEvents().first().getDatastoreId());
    assertEquals("Wrong saved event -- title", expectedEvent.getEventTitle(), result.getSavedEvents().first().getEventTitle());
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
  public void testGetIndividualRecommendedEventsNoRecommendedEventsReturned() throws URISyntaxException {
    List<Individual> users = this.individualRepository.findByUniversity(expectedIndividual.getUniversity());
    Recommender mockRecommender = Mockito.mock(Recommender.class);
    when(mockRecommender.recommend(any(Individual.class), any(List.class), any(), same(2))).thenReturn(new ArrayList<Event>());
    context.getBean(IndividualController.class).setRecommender(mockRecommender);
    // getting the actual result
    Event[] result = authRestTemplate.getForObject("/get-recommended-events-individual?count=2", Event[].class);
    Event[] expected = new Event[2];
    expected[0] = expectedEvent3;
    expected[1] = expectedEvent2;
    assertEquals("Wrong number returned", 2, result.length);
    assertEquals("Wrong events returned at position 0", expected[0], result[0]);
    assertEquals("Wrong events returned at position 1", expected[1], result[1]);
  }

  @Test
  public void testGetIndividualRecommendedEventsOneRecommendedEventsReturned() throws URISyntaxException {
    List<Individual> users = this.individualRepository.findByUniversity(expectedIndividual.getUniversity());
    Recommender mockRecommender = Mockito.mock(Recommender.class);
    List<Event> recommendedEvents = new ArrayList<Event>();
    recommendedEvents.add(expectedEvent2);
    when(mockRecommender.recommend(any(Individual.class), any(List.class), any(), same(2))).thenReturn(recommendedEvents);
    context.getBean(IndividualController.class).setRecommender(mockRecommender);
    // getting the actual result
    Event[] result = authRestTemplate.getForObject("/get-recommended-events-individual?count=2", Event[].class);
    Event[] expected = new Event[2];
    expected[0] = expectedEvent2;
    expected[1] = expectedEvent3;
    assertEquals("Wrong number returned", 2, result.length);
    assertEquals("Wrong events returned at position 0", expected[0], result[0]);
    assertEquals("Wrong events returned at position 1", expected[1], result[1]);
  }

  @Test
  public void testGetIndividualRecommendedEventsWithDuplicates() throws URISyntaxException {
    List<Individual> users = this.individualRepository.findByUniversity(expectedIndividual.getUniversity());
    Recommender mockRecommender = Mockito.mock(Recommender.class);
    List<Event> recommendedEvents = new ArrayList<Event>();
    recommendedEvents.add(expectedEvent2);
    recommendedEvents.add(expectedEvent3);
    when(mockRecommender.recommend(any(Individual.class), any(List.class), any(), same(4))).thenReturn(recommendedEvents);
    context.getBean(IndividualController.class).setRecommender(mockRecommender);
    // getting the actual result
    Event[] result = authRestTemplate.getForObject("/get-recommended-events-individual?count=4", Event[].class);
    Event[] expected = new Event[2];
    expected[0] = expectedEvent2;
    expected[1] = expectedEvent3;
    assertEquals("Wrong number returned", 2, result.length);
    assertEquals("Wrong events returned at position 0", expected[0], result[0]);
    assertEquals("Wrong events returned at position 1", expected[1], result[1]);
  }

  @Test
  public void testGetIndividualRecommendedEventsWithPastEvents() throws URISyntaxException {
    List<Individual> users = this.individualRepository.findByUniversity(expectedIndividual.getUniversity());
    Recommender mockRecommender = Mockito.mock(Recommender.class);
    when(mockRecommender.recommend(any(Individual.class), any(List.class), any(), same(4))).thenReturn(new ArrayList<Event>());
    context.getBean(IndividualController.class).setRecommender(mockRecommender);
    // getting the actual result
    Event[] result = authRestTemplate.getForObject("/get-recommended-events-individual?count=4", Event[].class);
    Event[] expected = new Event[2];
    expected[0] = expectedEvent3;
    expected[1] = expectedEvent2;
    assertEquals("Wrong number returned", 2, result.length);
    assertEquals("Wrong events returned at position 0", expected[0], result[0]);
    assertEquals("Wrong events returned at position 1", expected[1], result[1]);
  }


  @Test
  public void testGetIndividualRecommendedOrganizationsNoRecommendedOrganizationsReturned() throws URISyntaxException {
    List<Individual> users = this.individualRepository.findByUniversity(expectedIndividual.getUniversity());
    Recommender mockRecommender = Mockito.mock(Recommender.class);
    when(mockRecommender.recommend(any(Individual.class), any(List.class), any(), same(2))).thenReturn(new ArrayList<Organization>());
    context.getBean(IndividualController.class).setRecommender(mockRecommender);
    // getting the actual result
    Organization[] result = authRestTemplate.getForObject("/get-recommended-organizations-individual?count=2", Organization[].class);
    Organization[] expected = new Organization[2];
    expected[0] = expectedOrganization3;
    expected[1] = expectedOrganization2;
    assertEquals("Wrong number returned", 2, result.length);
    assertEquals("Wrong organizations returned at position 0", expected[0], result[0]);
    assertEquals("Wrong organizations returned at position 1", expected[1], result[1]);
  }

  @Test
  public void testGetIndividualRecommendedOrganizationsOneRecommendedOrganizationReturned() throws URISyntaxException {
    List<Individual> users = this.individualRepository.findByUniversity(expectedIndividual.getUniversity());
    Recommender mockRecommender = Mockito.mock(Recommender.class);
    List<Organization> recommendedOrganizations = new ArrayList<Organization>();
    recommendedOrganizations.add(expectedOrganization2);
    when(mockRecommender.recommend(any(Individual.class), any(List.class), any(), same(2))).thenReturn(recommendedOrganizations);
    context.getBean(IndividualController.class).setRecommender(mockRecommender);
    // getting the actual result
    Organization[] result = authRestTemplate.getForObject("/get-recommended-organizations-individual?count=2", Organization[].class);
    Organization[] expected = new Organization[2];
    expected[0] = expectedOrganization2;
    expected[1] = expectedOrganization3;
    assertEquals("Wrong number returned", 2, result.length);
    assertEquals("Wrong organizations returned at position 0", expected[0], result[0]);
    assertEquals("Wrong organizations returned at position 1", expected[1], result[1]);
  }

  @Test
  public void testGetIndividualRecommendedOrganizationsWithDuplicates() throws URISyntaxException {
    List<Individual> users = this.individualRepository.findByUniversity(expectedIndividual.getUniversity());
    Recommender mockRecommender = Mockito.mock(Recommender.class);
    List<Organization> recommendedOrganizations = new ArrayList<Organization>();
    recommendedOrganizations.add(expectedOrganization);
    recommendedOrganizations.add(expectedOrganization2);
    recommendedOrganizations.add(expectedOrganization3);
    when(mockRecommender.recommend(any(Individual.class), any(List.class), any(), same(4))).thenReturn(recommendedOrganizations);
    context.getBean(IndividualController.class).setRecommender(mockRecommender);
    // getting the actual result
    Organization[] result = authRestTemplate.getForObject("/get-recommended-organizations-individual?count=4", Organization[].class);
    Organization[] expected = new Organization[4];
    expected[0] = expectedOrganization;
    expected[1] = expectedOrganization2;
    expected[2] = expectedOrganization3;
    expected[3] = organizationSavedByUser;
    assertEquals("Wrong number returned", 4, result.length);
    assertEquals("Wrong organizations returned at position 0", expected[0], result[0]);
    assertEquals("Wrong organizations returned at position 1", expected[1], result[1]);
    assertEquals("Wrong organizations returned at position 2", expected[2], result[2]);
    assertEquals("Wrong organizations returned at position 3", expected[3], result[3]);
  }
}
