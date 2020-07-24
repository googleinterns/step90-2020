
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
import java.util.function.Function;

public class Recommender {

  public static <E, U> List<E> recommend (U targetUser, List<U> users, Function<U, List<E>> getItemList, int numNeeded) {
    Map<U, Integer> userToScore= new HashMap<U, Integer>();
    Set<E> targetUserEvents = new HashSet(getItemList.apply(targetUser));
    // for each user, calculate the total number of differences between the current user and the target user
    // store this result as the distance
    for (U user: users) {
      if (user.equals(targetUser)) {
        continue;
      }
      int dist = 0;
      Set<E> userEvents = new HashSet(getItemList.apply(user));
      for (E e : targetUserEvents) {
        if (!userEvents.contains(e)) {
          dist++;
        }
      }
      for (E e : userEvents) {
        if (!targetUserEvents.contains(e)) {
          dist++;
        }
      }
      userToScore.put(user, dist);
    }
    // sort the resulting list of individuals by increasing distance
    // Create a list from elements of HashMap
    List<Map.Entry<U, Integer>> sortedUsers =
            new LinkedList<Map.Entry<U, Integer> >(userToScore.entrySet());

    // Sort the list
    Collections.sort(sortedUsers, new Comparator<Map.Entry<U, Integer> >() {
      @Override
      public int compare(Map.Entry<U, Integer> o1,
                         Map.Entry<U, Integer> o2)
      {
        return (o1.getValue()).compareTo(o2.getValue());
      }
    });

    // put data from sorted user list to ordered event list
    List<E> sorted = new ArrayList<>();
    int count = 0;
    for (Map.Entry<U, Integer> entry : sortedUsers) {
      List<E> currUserEvents = getItemList.apply(entry.getKey());
      for (E e : currUserEvents) {
        if (!targetUserEvents.contains(e) && !sorted.contains(e) && count < numNeeded) {
          sorted.add(e);
          count++;
        }
      }
    }
    return sorted;
  }
}
