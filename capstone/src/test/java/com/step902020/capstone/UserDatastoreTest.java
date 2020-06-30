import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.step902020.capstone.*;
import java.util.*;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import com.google.gson.Gson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import java.util.Random;
import org.junit.Before;
import org.junit.After;
import java.net.URL;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = 
		CapstoneApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserDatastoreTest {
  @LocalServerPort
	private int port;

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
	private TestRestTemplate restTemplate;

  Individual expectedIndividual;
  Organization expectedOrganization;

  @Before
  public void setUp() {
    // append a random number to email to make a new user
    expectedIndividual = this.individualRepository.save(new Individual(System.currentTimeMillis(), "Jenny", "Sheng", "jennysheng@google.com" + new Random().nextInt(), "Princeton", "individual", ""));   
    expectedOrganization = this.organizationRepository.save(new Organization(System.currentTimeMillis(), "new organization", "newOrganization@google.com" + new Random().nextInt(), "Princeton", "organization", "hello world!", ""));    
 
  }

  @After
  public void tearDown() {
    this.individualRepository.deleteByEmail(expectedIndividual.getEmail());
    this.organizationRepository.deleteByEmail(expectedOrganization.getEmail());
  }

  @Test
  public void testGetIndividual() throws URISyntaxException {

    // getting the actual result
    restTemplate = new TestRestTemplate();
    String expectedEmail = expectedIndividual.getEmail();  
     
    final String baseUrl = "http://localhost:"+ port + "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
 
    Individual[] result = restTemplate.getForObject(uri, Individual[].class);

    Assert.assertEquals("Wrong user returned", expectedEmail, result[0].getEmail());
  }

  @Test
  public void testGetOrganization() throws URISyntaxException {

    restTemplate = new TestRestTemplate();
    String expectedEmail = expectedOrganization.getEmail();  
     
    final String baseUrl = "http://localhost:"+ port + "/get-organization?email=" + expectedEmail;
    URI uri = new URI(baseUrl);
 
    Organization[] result = restTemplate.getForObject(uri, Organization[].class);

    Assert.assertEquals("Wrong user returned", expectedEmail, result[0].getEmail());
  }

  @Test
  public void testAddEvent() throws URISyntaxException {

    String url = "http://localhost:" + port + "/add-saved-event";
    String expectedEmail = expectedIndividual.getEmail();  
 
    restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
    map.add("email", expectedEmail);
    map.add("event-name", "test");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

    ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class);

    // getting the actual result
    final String baseUrl = "http://localhost:"+ port + "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);

    Set<String> expectedSet = new HashSet<>();
    expectedSet.add("hello 2");
    expectedSet.add("hello 3");
    expectedSet.add("hello 1");
    expectedSet.add("test");
 
    Individual[] result = restTemplate.getForObject(uri, Individual[].class);

    Assert.assertEquals("Insert event error", expectedSet, result[0].getSavedEvents());
  }

  @Test
  public void testDeleteEvent() throws URISyntaxException {

    String url = "http://localhost:" + port + "/delete-saved-event";
    String expectedEmail = expectedIndividual.getEmail();  
 
    restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
    map.add("email", expectedEmail);
    map.add("event-name", "hello 2");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

    ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class);

    // getting the actual result
    final String baseUrl = "http://localhost:"+ port + "/get-individual?email=" + expectedEmail;
    URI uri = new URI(baseUrl);

    Set<String> expectedSet = new HashSet<>();
    expectedSet.add("hello 3");
    expectedSet.add("hello 1");
 
    Individual[] result = restTemplate.getForObject(uri, Individual[].class);

    Assert.assertEquals("Wrong user returned", expectedSet, result[0].getSavedEvents());
  }



  
}
