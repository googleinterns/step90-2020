package com.step902020.capstone.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

/**
 * Disables CSRF protection. This is something you would NEVER do for a production web project.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${spring.cloud.gcp.security.iap.enabled:true}")
  private boolean iapEnabled;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeRequests()
        .anyRequest().authenticated();

    if (iapEnabled) {
      http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    } else {
      http.formLogin().and().httpBasic();
    }
  }
}