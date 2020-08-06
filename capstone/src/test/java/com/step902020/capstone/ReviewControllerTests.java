package com.step902020.capstone;

import static org.junit.Assert.assertEquals;

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
public class ReviewControllerTests {
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

    Review expectedReview;

    @Before
    public void setUp() {
        expectedReview = new Review("John Smith", currentUserEmail, "10/10 Test Expected Review");
        this.reviewRepository.save(expectedReview);

        this.authRestTemplate = this.restTemplate
                .withBasicAuth(currentUserEmail, currentUserPassword);
    }

    @After
    public void tearDown() {
        this.reviewRepository.deleteByIndividualEmail(currentUserEmail);
    }

    @Test
    public void testLikeReview() throws URISyntaxException {
        String url = "/toggle-likes";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("reviewId", expectedReview.datastoreId);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);

        // getting the actual result
        Review result = reviewRepository.findById(expectedReview.datastoreId).orElse(null);
        assertEquals("Wrong review returned -- id", expectedReview.datastoreId, result.datastoreId);
        assertEquals("Wrong number of likes", 1, result.likes);
    }

    @Test
    public void testEditReview() throws URISyntaxException {
        String url = "/set-text";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("reviewId", expectedReview.datastoreId);
        map.add("newText", "10/10 Test Expected Reviewhi");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);

        // getting the actual result
        Review result = reviewRepository.findById(expectedReview.datastoreId).orElse(null);
        assertEquals("Wrong review returned -- id", expectedReview.datastoreId, result.datastoreId);
        assertEquals("Didn't update text", expectedReview.text + "hi", result.text);
    }
}
