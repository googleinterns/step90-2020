package com.step902020.capstone.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gcp.autoconfigure.core.environment.ConditionalOnGcpEnvironment;
import org.springframework.cloud.gcp.core.GcpEnvironment;
import org.springframework.cloud.gcp.security.iap.AudienceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class SecurityConfiguration {

  //@Bean
  //@ConditionalOnProperty("local")
  public AudienceProvider fakeAudienceProvider() {
    return () -> "fake-audience";
  }

}
