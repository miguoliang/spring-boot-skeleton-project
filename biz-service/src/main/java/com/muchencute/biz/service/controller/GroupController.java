package com.muchencute.biz.service.controller;


import com.muchencute.biz.keycloak.model.Group;
import com.muchencute.biz.keycloak.model.KeycloakGroup;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.request.MoveGroupRequest;
import com.muchencute.biz.keycloak.request.RenameGroupRequest;
import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import com.muchencute.biz.service.aop.bizlogger.BizLogger;
import com.muchencute.biz.service.aop.resolver.GetNameByGroupIdResolver;
import com.muchencute.biz.service.aop.resolver.Resolve;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
@RequestMapping("department")
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
  @PreAuthorize("hasAnyAuthority('department:crud')")
  public ResponseEntity<?> statGroup(@RequestParam String name,
                                     @RequestParam(required = false) String parentId) {

    if (groupRepository.exists(Example.of(KeycloakGroup.builder()
            .name(name).parentGroup(parentId).build()))) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @BizLogger(module = "部门管理", type = "新建",
          contentFormat = "新建部门【%s】",
          contentFormatArguments = @Resolve("response.name"),
          targetId = @Resolve("response.id"),
          targetName = @Resolve("response.name"),
          targetType = @Resolve("'部门'"))
  @PostMapping
  @Operation(summary = "新建部门")
  @PreAuthorize("hasAnyAuthority('department:crud')")
  public Group newGroup(@RequestBody Group group) {

    return keycloakGroupService.newGroup(group);
  }

  @BizLogger(module = "部门管理", type = "编辑",
          contentFormat = "编辑部门【%s】",
          contentFormatArguments = @Resolve("response.name"),
          targetId = @Resolve("request.path.id"),
          targetName = @Resolve("response.name"),
          targetType = @Resolve("'部门'"))
  @PutMapping("/{id}")
  @Operation(summary = "编辑部门")
  @PostMapping("/{id}:rename")
  @PreAuthorize("hasAnyAuthority('department:crud')")
  public Group renameGroup(@PathVariable String id, @RequestBody RenameGroupRequest request) {

    return keycloakGroupService.renameGroup(id, request.getNewGroupName());
  }

  @Operation(summary = "移动部门")
  @PostMapping("/{id}:move")
  @PreAuthorize("hasAnyAuthority('department:crud')")
  public Group moveGroup(@PathVariable String id, @RequestBody MoveGroupRequest request) {

    return keycloakGroupService.moveGroup(id, request.getParentId());
  }

  @GetMapping("/{id}")
  @Operation(summary = "查看部门")
  @PreAuthorize("hasAnyAuthority('department:crud')")
  public Group getGroup(@PathVariable String id) {

    return keycloakGroupService.getGroup(id);
  }

  @GetMapping
  @Operation(summary = "展示部门列表")
  @PreAuthorize("permitAll()")
  public List<Group> getGroups() {

    return keycloakGroupService.getGroups();
  }

  @BizLogger(module = "部门管理", type = "删除",
          contentFormat = "删除部门【%s】",
          contentFormatArguments = @Resolve(value = "request.path.id", resolver = GetNameByGroupIdResolver.class),
          targetId = @Resolve("request.path.id"),
          targetName = @Resolve("response.name"),
          targetType = @Resolve("'部门'"))
  @DeleteMapping("/{id}")
  @Operation(summary = "删除部门")
  @PreAuthorize("hasAnyAuthority('department:crud')")
  public void deleteGroup(@PathVariable String id) {

    keycloakGroupService.deleteGroup(id);
  }
}
