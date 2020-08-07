package com.step902020.capstone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
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
public class EventControllerTests {
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
    Event incorrectEvent;
    Event pastEvent;
    Review expectedReview;
    Review reviewAddedToEvent;
    University expectedUniversity;

    @Before
    public void setUp() {
        // Append a random number to email to make a new user
        expectedUniversity = new University("Test", 40.769579, -73.973036);
        this.universityRepository.save(expectedUniversity);

        Individual individual = new Individual(System.currentTimeMillis(), "UserWithOrganization",
                "ThatExists", currentUserEmail, expectedUniversity,"individual");
        expectedIndividual = this.individualRepository.save(individual);

        expectedOrganization = this.organizationRepository.save(
                new Organization(System.currentTimeMillis(),"new organization", currentUserEmail,
                        expectedUniversity, "organization", "hello world!", "service"));

        // Event dates will need to be updated after Aug 20, 2020
        expectedEvent = new Event(expectedUniversity,
                expectedOrganization.getDatastoreId(), "pizza party", "2020-08-20T12:30",
                "Turtles bring pizza",40.769579, -73.973036, "party",
                "2", "indoors", true, false, true);

        incorrectEvent = new Event(expectedUniversity,
                expectedOrganization.getDatastoreId(), "pizza making", "2020-08-20T12:30",
                "Turtles bring pizza",40.769579, -73.973036, "workshop",
                "2", "indoors", true, true, true);

        // Keep old date
        pastEvent = new Event(expectedUniversity,
                expectedOrganization.getDatastoreId(), "pizza party", "2020-07-20T12:30",
                "Turtles bring pizza",40.769579, -73.973036, "party",
                "2", "indoors", true, false, true);

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
    public void testGetFilteredEvents_AllParamsFilled() throws URISyntaxException {
        // All filterable params filled (all but eventTitle)
        Event[] result = createFilterTests(expectedUniversity.name, "", expectedEvent.eventType,
                expectedEvent.energyLevel, expectedEvent.location, String.valueOf(expectedEvent.foodAvailable),
                String.valueOf(expectedEvent.free), String.valueOf(expectedEvent.visitorAllowed));

        assertEquals("Filtered incorrectly", 1, result.length); // Should not include pastEvent
        assertTrue("Filtered incorrectly", result[0].equals(expectedEvent));
    }

    @Test
    public void testGetFilteredEvents_SomeParamsFilled() throws URISyntaxException {
        // Empty string in some expectedEvent's filterable params (acts as null when sending params from js to java)
        Event[] result = createFilterTests(expectedUniversity.name, "", expectedEvent.eventType,
                "", "", String.valueOf(expectedEvent.foodAvailable),
                String.valueOf(expectedEvent.free), "");
        assertEquals("Filtered incorrectly", 1, result.length);
        assertTrue("Filtered incorrectly", result[0].equals(expectedEvent));
    }

    @Test
    public void testGetFilteredEvents_EventSearchByName() throws URISyntaxException {
        // EventTitle param filled (invokes event search by name)
        Event[] result = createFilterTests(expectedUniversity.name, "pizza%20party", expectedEvent.eventType,
                expectedEvent.energyLevel, expectedEvent.location, String.valueOf(expectedEvent.foodAvailable),
                String.valueOf(expectedEvent.free), String.valueOf(expectedEvent.visitorAllowed));
        assertEquals("Filtered incorrectly", 1, result.length);
        assertTrue("Filtered incorrectly", result[0].equals(expectedEvent));
    }

    @Test
    public void testGetFilteredEvents_ParamsIncorrectlyFilled() throws URISyntaxException {
        // Non expectedEvent's filterable
        Event[] result = createFilterTests(expectedUniversity.name, "", "game",
                expectedEvent.energyLevel, expectedEvent.location, String.valueOf(expectedEvent.foodAvailable),
                String.valueOf(expectedEvent.free), String.valueOf(expectedEvent.visitorAllowed));
        assertEquals("Filtered incorrectly", 0, result.length);
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
        String url = "/add-event-review";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("reviewedObjectId", expectedEvent.datastoreId);
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

    @Test
    public void testGetOrganizationUniversity() throws  URISyntaxException {
        final String baseUrl = "/get-university-map?userType=organization";
        URI uri = new URI(baseUrl);
        University result = authRestTemplate.getForObject(uri, University.class);
        assertEquals("Incorrect University Displayed", expectedOrganization.getUniversity().getName(), result.getName());
    }

    @Test
    public void testGetIndividualUniversity() throws  URISyntaxException {
        final String baseUrl = "/get-university-map?userType=individual";
        URI uri = new URI(baseUrl);
        University result = authRestTemplate.getForObject(uri, University.class);
        assertEquals("Incorrect University Displayed", expectedIndividual.getUniversity().getName(), result.getName());
    }

    /**
     * Helper function for testGetFilteredEvents()
     * Fetches list of event based on url params
     * @param universityName university Name
     * @param eventTitle event title
     * @param eventType event type
     * @param energyLevel event energy level
     * @param location event location (indoors/outdoors)
     * @param foodAvailable if food available at event
     * @param free if event is free
     * @param visitorAllowed if visitors allowed at event
     * @return list of events from search
     * @throws URISyntaxException
     */
    private Event[] createFilterTests(String universityName, String eventTitle, String eventType, String energyLevel,
                                    String location, String foodAvailable, String free,
                                    String visitorAllowed) throws URISyntaxException {
        String baseUrl = "/get-filtered-events?universityName=" + universityName + "&eventTitle=" + eventTitle +
                "&eventType=" + eventType + "&energyLevel=" + energyLevel + "&location=" + location +
                "&foodAvailable=" + foodAvailable + "&free=" + free + "&visitorAllowed=" + visitorAllowed;
        URI uri = new URI(baseUrl);
        return authRestTemplate.getForObject(uri, Event[].class);
    }
}
