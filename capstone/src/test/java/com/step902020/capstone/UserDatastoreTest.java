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

}
