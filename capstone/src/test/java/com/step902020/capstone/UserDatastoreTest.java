package com.step902020.capstone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
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
  @Autowired private TestRestTemplate restTemplate;
  private TestRestTemplate authRestTemplate;

  Individual expectedIndividual;
  Organization expectedOrganization;

  @Before
  public void setUp() {
    // append a random number to email to make a new user

    expectedIndividual =
        this.individualRepository.save(
            new Individual(
                System.currentTimeMillis(),
                "Jenny",
                "Sheng",
                currentUserEmail,
                "Princeton",
                "individual",
                ""));
    expectedOrganization =
        this.organizationRepository.save(
            new Organization(
                System.currentTimeMillis(),
                "new organization",
                currentUserEmail,
                "Princeton",
                "organization",
                "hello world!",
                ""));

    this.authRestTemplate = this.restTemplate
        .withBasicAuth(currentUserEmail, currentUserPassword);
  }

  @After
  public void tearDown() {
    this.individualRepository.deleteByEmail(expectedIndividual.getEmail());
    this.individualRepository.deleteByEmail(currentUserEmail);
    this.organizationRepository.deleteByEmail(expectedOrganization.getEmail());
  }

  @Test
  public void testGetIndividual() throws URISyntaxException {
    // getting the actual result
    Individual[] result = authRestTemplate.getForObject("/get-individual", Individual[].class);
    assertEquals("Wrong user returned", currentUserEmail, result[0].getEmail());
  }

  @Test
  public void testGetOrganization() throws URISyntaxException {
    String expectedEmail = expectedOrganization.getEmail();
    final String baseUrl = "/get-organization";
    Organization[] result = authRestTemplate.getForObject(baseUrl, Organization[].class);
    assertEquals("Wrong user returned", expectedEmail, result[0].getEmail());
  }

  @Test
  public void testAddEvent() throws URISyntaxException {
    String url = "/add-saved-event";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("event-id", "123");
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> saveResponse = authRestTemplate.postForEntity(url, request, String.class);
    assertTrue(saveResponse.getHeaders().containsKey("Location"));
    String redirectLocation = saveResponse.getHeaders().getFirst("Location");
    assertTrue(redirectLocation.endsWith("savedevents.html"));

    // getting the actual result
    final String getIndividualUrl = "/get-individual";
    Set<Long> expectedSet = new HashSet<>();
    expectedSet.add(123L);

    Individual[] result = authRestTemplate.getForObject(getIndividualUrl, Individual[].class);
    assertEquals("Insert event error", expectedSet, result[0].getSavedEvents());
  }

  @Test
  public void testDeleteEvent() throws URISyntaxException {
    String url = "/delete-saved-event";
    String expectedEmail = expectedIndividual.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("email", expectedEmail);
    map.add("event-name", "hello 2");
    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);
    // getting the actual result
    final String baseUrl = "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
    Set<String> expectedSet = new HashSet<>();
    Individual[] result = authRestTemplate.getForObject(uri, Individual[].class);
    assertEquals("Delete event error", expectedSet, result[0].getSavedEvents());
  }

  @Test
  public void testAddSavedOrganization() throws URISyntaxException {
    String url = "/add-saved-organization";
    String expectedEmail = expectedIndividual.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("email", expectedEmail);
    map.add("organization-id", "234");
    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);
    // getting the actual result
    final String baseUrl = "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
    Set<Long> expectedSet = new HashSet<>();
    expectedSet.add(234L);
    Individual[] result = authRestTemplate.getForObject(uri, Individual[].class);
    assertEquals(
        "Insert organization error", expectedSet, result[0].getSavedOrganizations());
  }

  @Test
  public void testDeleteSavedOrganization() throws URISyntaxException {
    String url = "/delete-saved-organization";
    String expectedEmail = expectedIndividual.getEmail();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("email", expectedEmail);
    map.add("organization-email", "org1@google.com");
    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(map, headers);
    ResponseEntity<String> response = authRestTemplate.postForEntity(url, request, String.class);
    // getting the actual result
    final String baseUrl = "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
    Set<String> expectedSet = new HashSet<>();
    Individual[] result = authRestTemplate.getForObject(uri, Individual[].class);
    assertEquals(
        "Delete organization error", expectedSet, result[0].getSavedOrganizations());
  }
}
