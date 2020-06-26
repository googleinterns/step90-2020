
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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@RestController
public class UserServlet {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("get-user")
  public List<User> getUser(@RequestParam("email") String email) {
    return this.userRepository.findByEmail(email);
  }

  @PostMapping("save-user")
  public RedirectView saveUser(
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      @RequestParam("email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university,
      @RequestParam("description") String description) throws IOException {

    // first delete the old entry the add the new one
    this.userRepository.deleteByEmail(email);
    User current = new User(System.currentTimeMillis(), firstname, lastname, email, university, description, "");
    this.userRepository.save(current);
    return new RedirectView("/profile.html", true);
  }

  
}