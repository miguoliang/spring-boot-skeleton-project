package com.muchencute.biz.keycloak.service;

import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.model.Group;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class KeycloakGroupService {

  private final KeycloakService keycloakService;

  private final KeycloakGroupRepository keycloakGroupRepository;

  @Autowired
  public KeycloakGroupService(KeycloakService keycloakService,
                              KeycloakGroupRepository keycloakGroupRepository) {

    this.keycloakService = keycloakService;
    this.keycloakGroupRepository = keycloakGroupRepository;
  }

  public List<Group> getPosterity(String groupId) {

    if (groupId == null) {
      return keycloakGroupRepository
        .findAll()
        .stream()
        .map(it -> Group.builder()
          .id(it.getId())
          .name(it.getName())
          .build())
        .toList();
    }

    final var result = new LinkedList<Group>();
    final var groups = keycloakGroupRepository
      .findAllByParentGroup(groupId)
      .stream()
      .map(it -> Group.builder()
        .id(it.getId())
        .name(it.getName())
        .build())
      .toList();
    for (final var group : groups) {
      result.addAll(getPosterity(group.getId()));
    }
    result.addAll(groups);
    return result;
  }

  public Group newGroup(Group group) {

    final var groupId = keycloakService.newGroupResource(group.getName(),
      group.getParentId());
    return getGroup(groupId);
  }

  public Group renameGroup(String id, String newName) {

    keycloakService.renameGroupResource(id, newName);
    return getGroup(id);
  }

  public Group moveGroup(String id, String parentId) {

    keycloakService.moveGroupResource(id, parentId);
    return getGroup(id);
  }

  public Group getGroup(String id) {

    final var groupEntity = keycloakGroupRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Group not found"));
    return new Group(groupEntity.getId(), groupEntity.getName(), groupEntity.getParentGroup());
  }

  public List<Group> getGroups() {

    return keycloakGroupRepository
      .findAll()
      .stream()
      .map(it -> Group.builder()
        .id(it.getId())
        .name(it.getName())
        .parentId(it.getParentGroup())
        .build())
      .toList();
  }

  public void deleteGroup(String id) {

    final var groupResource = keycloakService.getGroupResource(id);
    groupResource.remove();
  }
}
