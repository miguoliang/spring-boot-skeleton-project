package com.muchencute.biz.service.controller;

import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.biz.keycloak.model.Group;
import com.muchencute.biz.keycloak.model.User;
import com.muchencute.biz.keycloak.model.UserAttribute;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.request.AcceptUserRequest;
import com.muchencute.biz.keycloak.request.NewUserRequest;
import com.muchencute.biz.keycloak.request.RejectUserRequest;
import com.muchencute.biz.keycloak.request.UpdateUserRequest;
import com.muchencute.biz.keycloak.service.KeycloakClientService;
import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import com.muchencute.biz.keycloak.service.KeycloakService;
import com.muchencute.biz.keycloak.service.KeycloakUserService;
import com.muchencute.biz.keycloak.validator.NotContainsSuperAdminUserId;
import com.muchencute.biz.keycloak.validator.NotSuperAdminUserId;
import com.muchencute.biz.service.NotificationService;
import com.muchencute.biz.service.aop.bizlogger.BizLogger;
import com.muchencute.biz.service.aop.resolver.GetNameByUserIdInPathResolver;
import com.muchencute.biz.service.aop.resolver.GetUserNameByUserIdInResolver;
import com.muchencute.biz.service.aop.resolver.Resolve;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "用户管理")
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

  private final KeycloakService keycloakService;

  private final KeycloakUserService keycloakUserService;

  private final KeycloakGroupService keycloakGroupService;

  private final UserEntityRepository userEntityRepository;

  private final NotificationService notificationService;

  private final KeycloakClientService keycloakClientService;

  @Autowired
  public UserController(KeycloakService keycloakService,
                        KeycloakUserService keycloakUserService,
                        KeycloakGroupService keycloakGroupService,
                        UserEntityRepository userEntityRepository,
                        NotificationService notificationService,
                        KeycloakClientService keycloakClientService) {

    this.keycloakService = keycloakService;
    this.keycloakUserService = keycloakUserService;
    this.keycloakGroupService = keycloakGroupService;
    this.userEntityRepository = userEntityRepository;
    this.notificationService = notificationService;
    this.keycloakClientService = keycloakClientService;
  }

  @RequestMapping(method = RequestMethod.HEAD)
  @Transactional("keycloakTransactionManager")
  public ResponseEntity<?> statUser(@RequestParam String username) {

    final var user = userEntityRepository.findByUsernameAndRealmId(username,
            keycloakService.getRealm());

    if (user.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    // 查询攻击检测状态
    final var map = keycloakService.getRealmResource().attackDetection()
            .bruteForceUserStatus(user.get().getId());
    log.info("BruteForceUserStatus: {}", map);

    final var attributes = user.get().getAttributes();
    final var status = attributes
            .stream()
            .filter(attribute -> attribute.getName().equals("status"))
            .findFirst()
            .map(UserAttribute::getValue)
            .orElse("normal");
    return ResponseEntity.ok()
            .header("x-amz-meta-status", status)
            .header("x-amz-meta-retries", map.getOrDefault("numFailures", 0).toString())
            .build();
  }

  @BizLogger(module = "用户管理", type = "新建",
          contentFormat = "新建用户【%s】",
          contentFormatArguments = @Resolve(value = "request.body.username"),
          targetId = @Resolve("response.id"),
          targetName = @Resolve(value = "request.body.username"),
          targetType = @Resolve("'用户'"))
  @PostMapping
  @Operation(summary = "新建用户")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  @SneakyThrows
  public User newUser(@Valid @RequestBody NewUserRequest request) {

    if (request.getUsername().startsWith("reserved_")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请务使用 reserved_ 开头命名！");
    }
    return keycloakUserService.newUser(request);
  }

  @BizLogger(module = "用户管理", type = "编辑",
          contentFormat = "编辑用户【%s】",
          contentFormatArguments = @Resolve(value = "request.path.id", resolver = GetUserNameByUserIdInResolver.class),
          targetId = @Resolve("request.path.id"),
          targetName = @Resolve(value = "request.path.id", resolver = GetUserNameByUserIdInResolver.class),
          targetType = @Resolve("'用户'"))
  @PutMapping("/{id}")
  @Operation(summary = "编辑用户")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  @SneakyThrows
  public User updateUser(@NotSuperAdminUserId @PathVariable String id,
                         @RequestBody UpdateUserRequest request) {

    return keycloakUserService.updateUser(id, request);
  }

  @GetMapping("/{id}")
  @Operation(summary = "用户查看")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  @SneakyThrows
  public User getUser(@PathVariable String id) {

    return keycloakUserService.getUser(id);
  }

  @GetMapping
  @Operation(summary = "展示用户")
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public Page<User> getUsers(@RequestParam(required = false) String groupId,
                             @RequestParam(required = false) String roleId,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false, name = "status") Set<String> statusSet,
                             Pageable pageable) {

    final var groups = new HashSet<String>();
    if (groupId != null) {
      groups.addAll(keycloakGroupService
              .getPosterity(groupId)
              .stream()
              .map(Group::getId)
              .toList());
      groups.add(groupId);
    }
    return keycloakUserService.getUsers(roleId, groups, keyword, statusSet, pageable);
  }

  @SneakyThrows
  @PostMapping("/{id}:enable")
  @Operation(summary = "启用用户")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  public void enableUser(@NotSuperAdminUserId @PathVariable("id") String id) {

    final var userResource = keycloakService.getUserResourceById(id);
    final var userRepresentation = userResource.toRepresentation();
    userRepresentation.setEnabled(true);
    userRepresentation.singleAttribute("status", "normal");
    userResource.update(userRepresentation);
  }

  @SneakyThrows
  @PostMapping("/{id}:disable")
  @Operation(summary = "禁用用户")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  public void disableUser(@NotContainsSuperAdminUserId @PathVariable("id") List<String> ids) {

    for (final var id : ids) {
      final var userResource = keycloakService.getUserResourceById(id);
      final var userRepresentation = userResource.toRepresentation();
      userRepresentation.setEnabled(false);
      userRepresentation.singleAttribute("status", "disable");
      userResource.update(userRepresentation);
    }
  }

  @BizLogger(
          type = "重置密码",
          module = "用户管理",
          contentFormat = "重置密码【%s】",
          contentFormatArguments = @Resolve(value = "request.path.id", resolver = GetNameByUserIdInPathResolver.class),
          targetId = @Resolve("request.path.id"),
          targetName = @Resolve(value = "request.path.id", resolver = GetNameByUserIdInPathResolver.class),
          targetType = @Resolve("'用户'")
  )
  @PostMapping("/{id}:reset-password")
  @Operation(summary = "重置密码")
  @PreAuthorize("hasAnyAuthority('user:reset_password')")
  @SneakyThrows
  public void resetUserPassword(@NotContainsSuperAdminUserId @PathVariable("id") List<String> ids) {

    for (final var id : ids) {
      keycloakUserService.resetUserCredentialById(id);
    }
  }

  @BizLogger(
          type = "删除",
          module = "用户管理",
          contentFormat = "删除用户【%s】",
          contentFormatArguments = @Resolve(value = "request.path.id", resolver = GetUserNameByUserIdInResolver.class),
          targetId = @Resolve("request.path.id"),
          targetName = @Resolve(value = "request.path.id", resolver = GetUserNameByUserIdInResolver.class),
          targetType = @Resolve("'用户'")
  )
  @DeleteMapping("/{id}")
  @Operation(summary = "删除用户")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  @SneakyThrows
  public void deleteUser(@NotSuperAdminUserId @PathVariable String id) {

    if (id.equalsIgnoreCase(JwtHelper.getUserId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能删除自己！");
    }

    keycloakUserService.deleteUserById(id);
  }

  @PostMapping("{id}/role/{roleName}")
  @Operation(summary = "批量调整角色")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  public void assignRole(@NotContainsSuperAdminUserId @PathVariable("id") List<String> ids,
                         @PathVariable String roleName) {

    for (final var id : ids) {
      keycloakService.detachAllRoleResourceByUserId(id);
      keycloakService.attachRoleResourceById(id, roleName);
    }
  }

  @PostMapping("{id}/group/{groupId}")
  @Operation(summary = "批量调整部门")
  @PreAuthorize("hasAnyAuthority('user:crud')")
  public void assignGroup(@NotContainsSuperAdminUserId @PathVariable("id") List<String> ids,
                          @PathVariable String groupId) {

    for (final var id : ids) {
      keycloakService.migrateGroup(id, groupId);
    }
  }

  @PostMapping("{id}:accept")
  @Operation(summary = "审批通过")
  @PreAuthorize("hasAnyAuthority('user:check')")
  public void acceptUser(@NotContainsSuperAdminUserId @PathVariable("id") List<String> ids,
                         @RequestBody AcceptUserRequest request) {

    for (final var id : ids) {
      keycloakService.acceptUser(id, request.getRoleName(), request.getMemo());
      final var username = keycloakUserService.getUser(id).getUsername();
      notificationService.createUserNotification(id, "系统消息",
              String.format("你的账号申请已通过，账号名：%s, 角色: %s", username, request.getRoleName()));
    }
  }

  @PostMapping("{id}:reject")
  @Operation(summary = "驳回")
  @PreAuthorize("hasAnyAuthority('user:check')")
  public void rejectUser(@NotContainsSuperAdminUserId @PathVariable("id") List<String> ids,
                         @RequestBody RejectUserRequest request) {

    for (final var id : ids) {
      keycloakService.rejectUser(id, request.getMemo());
      notificationService.createUserNotification(id, "系统消息",
              String.format("您的注册账号申请被驳回，审批意见：%s", request.getMemo()));
    }
  }

  @GetMapping("/onlineNum")
  @Operation(summary = "在线账号数")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Long> getOnlineUserNum() {

    return Map.of("count", keycloakClientService.getOnlineUserNum());
  }

  @GetMapping("/offlineNum")
  @Operation(summary = "离线账号数")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Long> getOfflineNum() {

    return Map.of("count", keycloakClientService.getOfflineUserNum());
  }
}
