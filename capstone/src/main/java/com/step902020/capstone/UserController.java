
package com.step902020.capstone;

import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import java.io.IOException;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@RestController
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("get-user")
  public List<User> getUser(@RequestParam("email") String email) {
    return this.userRepository.findByEmail(email);
  }

  @PostMapping("save-user")
  public RedirectView saveUser(
      @RequestParam("id") String id,
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      @RequestParam("email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university) throws IOException {
    
    User current = null;
    // depending on whether there is an id, either update or insert a new entity
    if (id.length() == 0) {
      current = new User(System.currentTimeMillis(), firstname, lastname, email, university, userType, "");
    } else {
      current = new User(Long.parseLong(id), System.currentTimeMillis(), firstname, lastname, email, university, userType, "");
    }
    this.userRepository.save(current);
    return new RedirectView("profile.html", true);
  }

  
}