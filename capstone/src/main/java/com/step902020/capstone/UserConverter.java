package com.step902020.capstone;

import org.springframework.core.convert.converter.Converter;
import java.util.*;

public class UserConverter {
	 static final Converter<List<String>, Set<String>> LIST_SET_CONVERTER =
            new Converter<List<String>, Set<String>>() {
                @Override
                public Set<String> convert(List<String> source) {
                    return new HashSet<>(source);
                }
            };
}