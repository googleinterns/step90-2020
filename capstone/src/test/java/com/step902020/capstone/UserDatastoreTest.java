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
  private UserRepository userRepository;

  @Autowired
	private TestRestTemplate restTemplate;

  User expected;

  @Test
  public void testGetUser() throws URISyntaxException {

    // getting the actual result
    restTemplate = new TestRestTemplate();
     
    final String baseUrl = "http://localhost:"+ port + "/get-user?email=" + expected.getEmail();
    URI uri = new URI(baseUrl);
 
    User[] result = restTemplate.getForObject(uri, User[].class);
    System.out.println(result[0]);

    String expectedEmail = expected.getEmail();  
    Assert.assertEquals("Wrong user returned", expectedEmail, result[0].getEmail());
  }

  @Before
  public void setUp() {
    // append a random number to email to make a new user
    expected = this.userRepository.save(new User(System.currentTimeMillis(), "Jenny", "Sheng", "jennysheng@google.com" + new Random().nextInt(), "Princeton", "hello", ""));    
  }

  @After
  public void tearDown() {
    this.userRepository.deleteByEmail(expected.getEmail());
  }

  public String convertToJson(List<User> users) {
    Gson gson = new Gson();
    String json = gson.toJson(users);
    return json;
  }


}
