package com.muchencute.biz.keycloak.controller;


import com.muchencute.biz.keycloak.model.Role;
import com.muchencute.biz.keycloak.repository.KeycloakRoleRepository;
import com.muchencute.biz.keycloak.request.NewRoleRequest;
import com.muchencute.biz.keycloak.service.KeycloakRoleService;
import com.muchencute.biz.keycloak.service.KeycloakService;
import com.muchencute.biz.keycloak.validator.NotProtectedUserOrRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("role")
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

  @PostMapping
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role newRole(@Valid @RequestBody NewRoleRequest newRoleRequest) {

    return roleService.newRole(newRoleRequest);
  }

  @PutMapping("{roleName}")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role updateRole(@PathVariable String roleName, @RequestBody Set<String> scopes) {

    return roleService.updateRole(roleName, scopes);
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<Role> getRoles() {

    return roleService.getRoles();
  }

  @GetMapping("{roleName}")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role getRole(@PathVariable String roleName) {

    return roleService.getRole(roleName);
  }

  @DeleteMapping("{roleName}")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public void deleteRole(@NotProtectedUserOrRole(fieldType = NotProtectedUserOrRole.FieldType.NAME,
    resourceType = NotProtectedUserOrRole.ResourceType.ROLE) @PathVariable String roleName) {

    keycloakService.getClientRoleResource(roleName).remove();
  }

  @PostMapping(path = "{roleName}:rename", consumes = "text/plain")
  @PreAuthorize("hasAnyAuthority('role:crud')")
  public Role renameRole(@PathVariable String roleName, @RequestBody String newRoleName) {

    return roleName.equals(newRoleName) ? roleService.getRole(roleName)
      : roleService.renameRole(roleName, newRoleName);
  }
}
