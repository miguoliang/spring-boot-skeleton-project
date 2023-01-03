package com.muchencute.biz.service.controller;


import com.muchencute.biz.keycloak.model.Role;
import com.muchencute.biz.keycloak.repository.KeycloakRoleRepository;
import com.muchencute.biz.keycloak.request.NewRoleRequest;
import com.muchencute.biz.keycloak.request.RenameRoleRequest;
import com.muchencute.biz.keycloak.service.KeycloakRoleService;
import com.muchencute.biz.keycloak.service.KeycloakService;
import com.muchencute.biz.keycloak.validator.NotSuperAdminRole;
import com.muchencute.biz.service.aop.bizlogger.BizLogger;
import com.muchencute.biz.service.aop.resolver.Resolve;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("role")
@Slf4j
@Validated
public class RoleController {

  private final KeycloakRoleService roleService;

  private final KeycloakService keycloakService;

  private final KeycloakRoleRepository roleRepository;

  @Autowired
  public RoleController(KeycloakRoleService roleService, KeycloakService keycloakService,
                        KeycloakRoleRepository roleRepository) {

    this.roleService = roleService;
    this.keycloakService = keycloakService;
    this.roleRepository = roleRepository;
  }

  @RequestMapping(method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public ResponseEntity<?> statRole(@RequestParam String roleName) {

    if (roleRepository.existsByNameAndClientRole(roleName, true)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @BizLogger(
          module = "角色管理",
          type = "新建",
          contentFormat = "新建角色【%s】",
          contentFormatArguments = @Resolve("response.name"),
          targetId = @Resolve("response.name"),
          targetName = @Resolve("response.name"),
          targetType = @Resolve("'角色'"))
  @PostMapping
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role newRole(@Valid @RequestBody NewRoleRequest newRoleRequest) {

    return roleService.newRole(newRoleRequest);
  }

  @BizLogger(
          module = "角色管理",
          type = "编辑",
          contentFormat = "编辑角色【%s】",
          contentFormatArguments = @Resolve("response.name"),
          targetId = @Resolve("response.name"),
          targetName = @Resolve("response.name"),
          targetType = @Resolve("'角色'"))
  @PutMapping("{roleName}")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role updateRole(@NotSuperAdminRole @PathVariable String roleName,
                         @RequestBody Set<String> scopes) {

    return roleService.updateRole(roleName, scopes);
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<Role> getRoles() {

    return roleService.getRoles()
            .stream()
            .sorted((a, b) -> {
              if (a.getName().equals("超级管理员")) {
                return -1;
              } else if (b.getName().equals("超级管理员")) {
                return 1;
              } else {
                return a.getName().compareTo(b.getName());
              }
            })
            .toList();
  }

  @GetMapping("{roleName}")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role getRole(@PathVariable String roleName) {

    return roleService.getRole(roleName);
  }

  @BizLogger(
          module = "角色管理",
          type = "删除",
          contentFormat = "删除角色【%s】",
          contentFormatArguments = @Resolve("request.path.roleName"),
          targetId = @Resolve("request.path.roleName"),
          targetName = @Resolve("request.path.roleName"),
          targetType = @Resolve("'角色'"))
  @DeleteMapping("{roleName}")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public void deleteRole(@NotSuperAdminRole @PathVariable String roleName) {

    keycloakService.getClientRoleResource(roleName).remove();
  }

  @BizLogger(
          module = "角色管理",
          type = "编辑",
          contentFormat = "编辑角色【%s】",
          contentFormatArguments = @Resolve("request.body.newRoleName"),
          targetId = @Resolve("request.body.newRoleName"),
          targetName = @Resolve("request.body.newRoleName"),
          targetType = @Resolve("'角色'"))
  @PostMapping("{roleName}:rename")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role renameRole(@NotSuperAdminRole @PathVariable String roleName,
                         @RequestBody RenameRoleRequest renameRoleRequest) {

    return roleName.equals(renameRoleRequest.getNewRoleName()) ? roleService.getRole(roleName)
            : roleService.renameRole(roleName, renameRoleRequest.getNewRoleName());
  }
}
