
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

  public static <E, U> List<E> recommend (U targetUser, List<U> users, Function<U, List<E>> getItemList) {
    users.remove(targetUser);
    Map<U, Double> userToScore= new HashMap<U, Double>();
    Set<E> targetUserEvents = new HashSet(getItemList.apply(targetUser));
    for (U user: users) {
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
      double scaledDist = 1/(1+Math.sqrt(dist));
      userToScore.put(user, scaledDist);
    }
    // sort the resulting list of individuals by increasing distance
    // Create a list from elements of HashMap
    List<Map.Entry<U, Double>> tempList =
            new LinkedList<Map.Entry<U, Double> >(userToScore.entrySet());

    // Sort the list
    Collections.sort(tempList, new Comparator<Map.Entry<U, Double> >() {
      @Override
      public int compare(Map.Entry<U, Double> o1,
                         Map.Entry<U, Double> o2)
      {
        return (o1.getValue()).compareTo(o2.getValue());
      }
    });

    // put data from sorted list to hashmap
    List<E> sorted = new ArrayList<>();
    int count = 0;
    for (Map.Entry<U, Double> entry : tempList) {
      List<E> currUserEvents = getItemList.apply(entry.getKey());
      for (E e : currUserEvents) {
        if (!targetUserEvents.contains(e) && !sorted.contains(e) && count < 10) {
          sorted.add(e);
          count++;
        }
      }
    }
    return sorted;
  }
}
