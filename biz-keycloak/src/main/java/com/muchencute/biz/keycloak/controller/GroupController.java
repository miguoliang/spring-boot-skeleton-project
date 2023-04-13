package com.muchencute.biz.keycloak.controller;


import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.keycloak.exception.ConflictException;
import com.muchencute.biz.keycloak.model.Group;
import com.muchencute.biz.keycloak.model.KeycloakGroup;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "部门管理")
@RestController
@RequestMapping("group")
@ConditionalOnProperty(name = "keycloak.use-endpoints", matchIfMissing = true)
public class GroupController {

  private final KeycloakGroupService keycloakGroupService;

  private final KeycloakGroupRepository groupRepository;

  @Autowired
  public GroupController(KeycloakGroupService keycloakGroupService,
                         KeycloakGroupRepository groupRepository) {

    this.keycloakGroupService = keycloakGroupService;
    this.groupRepository = groupRepository;
  }

  @RequestMapping(method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthority('group:crud')")
  public ResponseEntity<?> statGroup(@RequestParam String name,
                                     @RequestParam(required = false) String parentId) {

    if (groupRepository.exists(Example.of(KeycloakGroup.builder()
      .name(name).parentGroup(parentId).build()))) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @Operation(summary = "新建部门")
  @PreAuthorize("hasAnyAuthority('group:crud')")
  public Group newGroup(@RequestBody Group group) {

    return keycloakGroupService.newGroup(group);
  }

  @Operation(summary = "编辑部门")
  @PostMapping(path = "/{id}:rename", consumes = "text/plain")
  @PreAuthorize("hasAnyAuthority('group:crud')")
  public Group renameGroup(@PathVariable String id, @RequestBody String newGroupName) {

    return keycloakGroupService.renameGroup(id, newGroupName);
  }

  @Operation(summary = "移动部门")
  @PostMapping(path = "/{id}:move", consumes = "text/plain")
  @PreAuthorize("hasAnyAuthority('group:crud')")
  public Group moveGroup(@PathVariable String id, @RequestBody(required = false) String parentId) {

    final var cycled = keycloakGroupService.getPosterity(id)
      .stream()
      .anyMatch(it -> it.getId().equalsIgnoreCase(parentId));
    if (cycled || id.equalsIgnoreCase(parentId)) {
      throw new BadRequestException("不能移动到自己或自己的子部门下。");
    }

    if (parentId != null && !groupRepository.existsById(parentId)) {
      throw new NotFoundException("父部门不存在。");
    }

    final var groupName = keycloakGroupService.getGroup(id).getName();
    final var targetSubGroupNames =
      groupRepository.findAllByParentGroup(StrUtil.nullToDefault(parentId, " "))
        .stream()
        .map(KeycloakGroup::getName)
        .collect(Collectors.toSet());

    if (targetSubGroupNames.contains(groupName)) {
      throw new ConflictException("父部门下已存在同名部门。");
    }

    return keycloakGroupService.moveGroup(id, parentId);
  }

  @GetMapping("/{id}")
  @Operation(summary = "查看部门")
  @PreAuthorize("hasAnyAuthority('group:crud')")
  public Group getGroup(@PathVariable String id) {

    return keycloakGroupService.getGroup(id);
  }

  @GetMapping
  @Operation(summary = "展示部门列表")
  @PreAuthorize("permitAll()")
  public List<Group> getGroups() {

    return keycloakGroupService.getGroups();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "删除部门")
  @PreAuthorize("hasAnyAuthority('group:crud')")
  public void deleteGroup(@PathVariable String id) {

    keycloakGroupService.deleteGroup(id);
  }
}
