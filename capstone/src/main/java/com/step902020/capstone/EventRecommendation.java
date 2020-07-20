package com.step902020.capstone;

public class EventRecommendation {
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
}
