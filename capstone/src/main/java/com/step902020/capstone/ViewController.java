package com.step902020.capstone;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

  @GetMapping("/{page}.html*")
  public String singlePathVariable(@PathVariable("page") String page) {
    return page;
  }
}

