package com.step902020.capstone;

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class RecommenderTests {

  public List<String> getListOfItems(String user) {
    List<String> result = new ArrayList<String>();
    if (user.equals("A")) {
      result.add("event 1");
      result.add("event 3");
    } else if (user.equals("B")) {
      result.add("event 1");
      result.add("event 2");
    } else if (user.equals("C")) {
      result.add("event 2");
      result.add("event 4");
    }
    return result;
  }

  @Test
  public void testRecommendWithTwoPeopleCase() throws URISyntaxException {
    List<String> list = new ArrayList<String>();
    list.add("B");
    List<String> result = Recommender.recommend("A", list, p  -> getListOfItems(p), 1);
    List<String> expected = new ArrayList<>();
    expected.add("event 2");
    assertEquals("Wrong list returned", expected, result);
  }

  @Test
  public void testRecommendWithThreePeopleCase() throws URISyntaxException {
    List<String> list = new ArrayList<String>();
    list.add("B");
    list.add("C");
    List<String> result = Recommender.recommend("A", list, p  -> getListOfItems(p), 2);
    List<String> expected = new ArrayList<>();
    expected.add("event 2");
    expected.add("event 4");
    assertEquals("Wrong list returned", expected, result);
  }

  @Test
  public void testRecommendWithTwoPeopleCaseWithRepeat() throws URISyntaxException {
    List<String> list = new ArrayList<String>();
    list.add("A");
    list.add("B");
    List<String> result = Recommender.recommend("A", list, p  -> getListOfItems(p), 1);
    List<String> expected = new ArrayList<>();
    expected.add("event 2");
    assertEquals("Wrong list returned", expected, result);
  }
}