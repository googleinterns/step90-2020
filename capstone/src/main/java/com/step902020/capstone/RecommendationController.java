
package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import java.io.IOException;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@RestController
public class RecommendationController {

  @Autowired
  private IndividualRepository individualRepository;

  @Autowired
  private EventRepository eventRepository;


  @GetMapping("recommend-events")
  public List<Event> recommendEvents(CurrentUser currentUser) {
    Individual targetUser = this.individualRepository.findFirstByEmail(currentUser.getEmail());
    List<Individual> users = this.individualRepository.findByUniversity(targetUser.getUniversity());
    users.remove(targetUser);
    return findRecommendedEvents(targetUser, users);
  }

  public List<Event> findRecommendedEvents (Individual targetUser, List<Individual> users) {
    Map<Long, Double> userToScore= new HashMap<Long, Double>();
    Set<Event> targetUserEvents = new HashSet(targetUser.getSavedEvents());
    for (Individual user: users) {
      int dist = 0;
      Set<Event> userEvents = new HashSet(user.getSavedEvents());
      for (Event e : targetUserEvents) {
        if (!userEvents.contains(e)) {
          dist++;
        }
      }
      for (Event e : userEvents) {
        if (!targetUserEvents.contains(e)) {
          dist++;
        }
      }
      double scaledDist = 1/(1+Math.sqrt(dist));
      userToScore.put(user.getDatastoreId(), scaledDist);
    }
    // sort the resulting list of individuals by increasing distance
    // Create a list from elements of HashMap
    List<Map.Entry<Long, Double>> tempList =
            new LinkedList<Map.Entry<Long, Double> >(userToScore.entrySet());

    // Sort the list
    Collections.sort(tempList, new Comparator<Map.Entry<Long, Double> >() {
      @Override
      public int compare(Map.Entry<Long, Double> o1,
                         Map.Entry<Long, Double> o2)
      {
        return (o1.getValue()).compareTo(o2.getValue());
      }
    });

    // put data from sorted list to hashmap
    List<Event> sorted = new ArrayList<>();
    for (Map.Entry<Long, Double> entry : tempList) {
      List<Event> currUserEvents = this.individualRepository.findById(entry.getKey()).orElse(null).getSavedEvents();
      for (Event e : currUserEvents) {
        if (!targetUserEvents.contains(e)) {
          sorted.add(e);
        }
      }

    }
    return sorted;
  }
}
  /*
  List listOfUsers = list of users in the same college (excluding the current user)
  User targetUser = current user being considered
  Map userToScore = map that maps users to the distance (or score they get), or an arraylist of Pairs (to make it easier to sort later)
  for user in listOfUsers {
    int dist = 0
    Set targetUserEvents = event of the current user
    Set userEvents = event of the other user being considered right now

    for event in targetUserEvents {
      if (not in userEvents) { dist++; }
    }
    for event in userEvents {
      if (not in targetUserEvents) { dist++;}
    }
    put score in map
  }

  sort userToScore ascending
  List eventsRecommended = list to contain the events recommended
  for user in userToScore {
    find the events saved by user that is not saved by target user
    add to eventsRecommended;
  }
  return eventsRecommended;

   */
