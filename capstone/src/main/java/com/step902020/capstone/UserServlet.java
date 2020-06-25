
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
  public User getUser(@RequestParam("email") String email) {
    User curr = this.userRepository.findByEmail("jennysheng@google.com").get(0);
    // User curr = new User(System.currentTimeMillis(), "Jenny", "Sheng", "js112@princeton.edu", "princeton", "hello world", "");
    return curr;
  }

  @PostMapping("save-user")
  public RedirectView saveUser(
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname, 
      @RequestParam("email") String email, 
      @RequestParam("user-type") String userType,
      @RequestParam("university") String university,
      @RequestParam("description") String description) throws IOException {
          
    this.userRepository.save(new User(System.currentTimeMillis(), firstname, lastname, email, university, description, ""));

    return new RedirectView("/");
  }

  @GetMapping("authenticate")
  public HashMap<String, String> authenticate(@RequestParam("redirect") String redirect) {
    UserService userService = UserServiceFactory.getUserService();
    HashMap<String, String> result = new HashMap<String, String>();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/" + redirect + ".html";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      result.put("user", userEmail);
      result.put("url", logoutUrl);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/" + redirect + ".html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      result.put("user", "Stranger");
      result.put("url", loginUrl);
    }
    return result;
  }
}