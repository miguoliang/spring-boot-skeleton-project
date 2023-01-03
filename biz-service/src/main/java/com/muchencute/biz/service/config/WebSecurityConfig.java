package com.muchencute.biz.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * About Security Annotations <<a
 * href="https://stackoverflow.com/questions/43961625/rolesallowed-vs-preauthorize-vs-secured">...</a>>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

    // @formatter:off
    return httpSecurity
        .authorizeHttpRequests()
        .anyRequest()
        .permitAll()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .cors()
        .and()
        .csrf().disable()
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(jwtAuthenticationConverter())
        .and()
        .and()
        .build();
    // @formatter:on
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {

    final var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
    final var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}