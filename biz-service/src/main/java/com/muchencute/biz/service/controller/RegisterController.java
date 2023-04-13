package com.muchencute.biz.service.controller;

import com.muchencute.biz.keycloak.model.User;
import com.muchencute.biz.keycloak.request.RegisterUserRequest;
import com.muchencute.biz.keycloak.service.KeycloakUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "新用户注册")
@RestController
@RequestMapping("register")
public class RegisterController {

  private final KeycloakUserService keycloakUserService;

  @Autowired
  public RegisterController(KeycloakUserService keycloakUserService) {

    this.keycloakUserService = keycloakUserService;
  }

  @PostMapping
  @Operation(summary = "新用户注册")
  @PreAuthorize("isAnonymous()")
  public User register(@RequestBody RegisterUserRequest request) {

    if (request.getUsername().startsWith("reserved_")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请务使用 reserved_ 开头命名！");
    }
    final var userId = keycloakUserService.registerUser(request);
    return keycloakUserService.getUser(userId);
  }
}
