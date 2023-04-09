package com.muchencute.biz.keycloak.environment.service;

import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;

import java.util.Collections;

@Service
public class KeycloakAccessTokenService {

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.client-id}")
  private String clientId;

  @SneakyThrows
  public void login(GenericContainer<?> keycloakContainer, String username, String password) {

    final var url = String.format("http://%s:%d", keycloakContainer.getHost(),
      keycloakContainer.getMappedPort(8080));
    final var authorizationURI = new URIBuilder(url +
      "/auth/realms/" + realm + "/protocol/openid-connect/token").build();
    final var webclient = WebClient.builder().build();
    final var formData = new LinkedMultiValueMap<String, String>();
    formData.put("grant_type", Collections.singletonList("password"));
    formData.put("client_id", Collections.singletonList(clientId));
    formData.put("username", Collections.singletonList(username));
    formData.put("password", Collections.singletonList(password));

    webclient.post()
      .uri(authorizationURI)
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .body(BodyInserters.fromFormData(formData))
      .retrieve()
      .bodyToMono(String.class)
      .block();
  }
}
