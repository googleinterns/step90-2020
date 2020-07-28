package com.step902020.capstone;

import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserConverter {
	 static final Converter<List<String>, Set<String>> LIST_SET_CONVERTER =
            new Converter<List<String>, Set<String>>() {
                @Override
                public Set<String> convert(List<String> source) {
                    return new HashSet<>(source);
                }
            };

  static final Converter<List<Event>, TreeSet<Event>> LIST_TREESET_CONVERTER_EVENTS =
          new Converter<List<Event>, TreeSet<Event>>() {
            @Override
            public TreeSet<Event> convert(List<Event> source) {
              TreeSet<Event> treeSet = new TreeSet<Event>((a, b) ->
                      a.getEventTitle().toLowerCase().compareTo(b.getEventTitle().toLowerCase()));
              treeSet.addAll(source);
              return treeSet;
            }
          };

  static final Converter<List<Organization>, TreeSet<Organization>> LIST_TREESET_CONVERTER_ORGS =
          new Converter<List<Organization>, TreeSet<Organization>>() {
            @Override
            public TreeSet<Organization> convert(List<Organization> source) {
              TreeSet<Organization> treeSet = new TreeSet<Organization>((a, b) ->
                      a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));
              treeSet.addAll(source);
              return treeSet;
            }
          };

}