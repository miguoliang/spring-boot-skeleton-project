package com.muchencute.biz.service.controller;

import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.biz.keycloak.model.User;
import com.muchencute.biz.keycloak.request.ResetPasswordRequest;
import com.muchencute.biz.keycloak.service.KeycloakUserService;
import com.muchencute.biz.model.Notification;
import com.muchencute.biz.service.NotificationService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profile")
// 此处用 MyProfileController 命名是因为 Spring Data Rest 中有一个同名的 Controller，为了避免冲突导致启动失败
public class MyProfileController {

  private final KeycloakUserService keycloakUserService;

  private final NotificationService notificationService;

  @Autowired
  public MyProfileController(KeycloakUserService keycloakUserService,
                             NotificationService notificationService) {

    this.keycloakUserService = keycloakUserService;
    this.notificationService = notificationService;
  }

  @PutMapping
  @PreAuthorize("isAuthenticated()")
  public User updateUser(@RequestBody User user) {

    return keycloakUserService.updateProfile(user);
  }

  @PostMapping("reset-password")
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public void resetPassword(@RequestBody ResetPasswordRequest request) {

    final var username = JwtHelper.getUsername();
    keycloakUserService.resetUserCredential(username, request.getOriginalPassword(),
            request.getPassword());
  }

  @PostMapping("notification:read")
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public void markNotificationIsRead() {

    final var userId = JwtHelper.getUserId();
    notificationService.markAllAsRead(userId);
  }

  @GetMapping("notification")
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public Page<Notification> getNotifications(
          @RequestParam(defaultValue = "false") Boolean unreadOnly, Pageable pageable) {

    final var userId = JwtHelper.getUserId();
    return notificationService.getNotifications(userId, unreadOnly, pageable);
  }
}
