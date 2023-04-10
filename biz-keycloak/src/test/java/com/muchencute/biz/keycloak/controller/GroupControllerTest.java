package com.muchencute.biz.keycloak.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchencute.biz.keycloak.environment.KeycloakTestEnvironment;
import com.muchencute.biz.keycloak.environment.mock.MockGroup;
import com.muchencute.biz.keycloak.model.Group;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin", authorities = "group:crud")
@ContextConfiguration(classes = GroupController.class)
class GroupControllerTest extends KeycloakTestEnvironment {

  final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  KeycloakGroupService keycloakGroupService;

  @Autowired
  KeycloakGroupRepository keycloakGroupRepository;

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_3", parent = "group_2")
  void group_move_to_root() {

    final var groupToMove = keycloakGroupRepository.findByName("group_3")
      .stream().findFirst().orElseThrow().getId();
    mockMvc.perform(post("/group/{groupId}:move", groupToMove)
        .contentType(MediaType.TEXT_PLAIN))
      .andExpect(status().isOk())
      .andDo(document("group/move/to-root"));

    Assertions.assertEquals(2, keycloakGroupService.getGroups()
      .stream().filter(it -> StrUtil.isBlank(it.getParentId())).count());

    mockMvc.perform(get("/group"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()", equalTo(3)))
      .andExpect(jsonPath("$.[?(@.name=='group_3')].parentId", hasSize(1)))
      .andExpect(jsonPath("$.[?(@.name=='group_3')].parentId", contains(nullValue())));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_a")
  @MockGroup(name = "group_b", parent = "group_a")
  void group_move_into_another() {

    final var groupToMove = keycloakGroupRepository.findByName("group_1").orElseThrow();
    final var targetGroup = keycloakGroupRepository.findByName("group_b").orElseThrow();

    mockMvc.perform(post("/group/{groupId}:move", groupToMove.getId())
        .contentType(MediaType.TEXT_PLAIN)
        .content(targetGroup.getId()))
      .andExpect(status().isOk())
      .andDo(document("group/move/into-another"));

    Assertions.assertEquals(1, keycloakGroupService.getGroups()
      .stream().filter(it -> StrUtil.isBlank(it.getParentId())).count());

    mockMvc.perform(get("/group"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()", equalTo(4)))
      .andExpect(jsonPath("$.[?(@.name=='group_a')].parentId", hasSize(1)))
      .andExpect(jsonPath("$.[?(@.name=='group_a')].parentId", contains(nullValue())))
      .andExpect(jsonPath("$.[?(@.name=='group_1')].parentId", hasSize(1)))
      .andExpect(jsonPath("$.[?(@.name=='group_1')].parentId", contains(targetGroup.getId())));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  void group_move_target_not_exists() {

    final var groupToMove = keycloakGroupRepository.findByName("group_1").orElseThrow();

    mockMvc.perform(post("/group/{groupId}:move", groupToMove.getId())
        .contentType(MediaType.TEXT_PLAIN)
        .content("not-exists"))
      .andExpect(status().isNotFound())
      .andDo(document("group/move/target-not-exists"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_2")
  void group_move_to_another_with_same_name_should_fail() {

    final var groupToMove = keycloakGroupRepository.findByName("group_1").orElseThrow();
    final var targetGroup = keycloakGroupRepository.findByNameAndParentGroup("group_2", " ").orElseThrow();

    mockMvc.perform(post("/group/{groupId}:move", targetGroup.getId())
        .contentType(MediaType.TEXT_PLAIN)
        .content(groupToMove.getId()))
      .andExpect(status().isConflict())
      .andDo(document("group/move/to-another-with-same-name-should-fail"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_2")
  void group_move_to_root_with_same_name_should_fail() {

    final var group1Id = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    final var sourceGroupId = keycloakGroupRepository
      .findByNameAndParentGroup("group_2", group1Id).orElseThrow().getId();

    mockMvc.perform(post("/group/{groupId}:move", sourceGroupId)
        .contentType(MediaType.TEXT_PLAIN))
      .andExpect(status().isConflict())
      .andDo(document("group/move/to-root-with-same-name-should-fail"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_3", parent = "group_2")
  void group_move_to_itself_should_fail() {

    final var groupToMove = keycloakGroupRepository.findByName("group_1").orElseThrow();

    mockMvc.perform(post("/group/{groupId}:move", groupToMove.getId())
        .contentType(MediaType.TEXT_PLAIN)
        .content(groupToMove.getId()))
      .andExpect(status().isBadRequest())
      .andDo(document("group/move/to-itself-should-fail"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_3", parent = "group_2")
  void group_move_to_posterity_should_fail() {

    final var groupToMove = keycloakGroupRepository.findByName("group_1").orElseThrow();
    final var group2Id = keycloakGroupRepository.findByName("group_2").orElseThrow().getId();

    mockMvc.perform(post("/group/{groupId}:move", groupToMove.getId())
        .contentType(MediaType.TEXT_PLAIN)
        .content(group2Id))
      .andExpect(status().isBadRequest())
      .andDo(document("group/move/to-posterity-should-fail"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  void group_stat_exists() {

    mockMvc.perform(head("/group")
        .param("name", "group_1"))
      .andExpect(status().isOk())
      .andDo(document("group/stat/exists"));
  }

  @Test
  @SneakyThrows
  void group_stat_not_exists() {
    mockMvc.perform(head("/group")
        .param("name", "group_1"))
      .andExpect(status().isNotFound())
      .andDo(document("group/stat/not-exists"));
  }

  @Test
  @SneakyThrows
  void group_new_root() {

    final var newGroup = new Group();
    newGroup.setName("group_1");

    final var responseText = mockMvc.perform(post("/group")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newGroup)))
      .andExpect(status().isOk())
      .andDo(document("group/new/root"))
      .andReturn()
      .getResponse()
      .getContentAsString(StandardCharsets.UTF_8);

    final var responseGroup = objectMapper.readValue(responseText, Group.class);
    Assertions.assertFalse(StrUtil.isBlank(responseGroup.getId()));
    Assertions.assertEquals("group_1", responseGroup.getName());
    Assertions.assertNull(responseGroup.getParentId());

    mockMvc.perform(get("/group/" + responseGroup.getId()))
      .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_root")
  void group_new_root_exists() {

    final var newGroup = new Group();
    newGroup.setName("group_root");

    mockMvc.perform(post("/group")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newGroup)))
      .andExpect(status().isConflict())
      .andDo(document("group/new/root-exists"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_root")
  void group_new_child_ok() {

    final var newGroup = new Group();
    newGroup.setName("group_child");
    final var rootGroupId = keycloakGroupRepository.findByName("group_root").orElseThrow().getId();
    newGroup.setParentId(rootGroupId);

    final var responseText = mockMvc.perform(post("/group")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newGroup)))
      .andExpect(status().isOk())
      .andDo(document("group/new/child-ok"))
      .andReturn()
      .getResponse()
      .getContentAsString(StandardCharsets.UTF_8);

    final var responseGroup = objectMapper.readValue(responseText, Group.class);
    Assertions.assertFalse(StrUtil.isBlank(responseGroup.getId()));
    Assertions.assertEquals("group_child", responseGroup.getName());
    Assertions.assertEquals(rootGroupId, responseGroup.getParentId());

    mockMvc.perform(get("/group/" + responseGroup.getId()))
      .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  void group_new_child_exists() {

    final var newGroup = new Group();
    newGroup.setName("group_2");
    final var rootGroupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    newGroup.setParentId(rootGroupId);

    mockMvc.perform(post("/group")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newGroup)))
      .andExpect(status().isConflict())
      .andDo(document("group/new/child-exists"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  void group_rename_ok() {

    final var groupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    mockMvc.perform(post("/group/{groupId}:rename", groupId)
        .contentType(MediaType.TEXT_PLAIN)
        .content("group_2"))
      .andExpect(status().isOk())
      .andDo(document("group/rename/ok"));

    mockMvc.perform(get("/group/" + groupId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", equalTo(groupId)))
      .andExpect(jsonPath("$.name", equalTo("group_2")));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2")
  void group_rename_exists() {

    final var groupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    mockMvc.perform(post("/group/{groupId}:rename", groupId)
        .contentType(MediaType.TEXT_PLAIN)
        .content("group_2"))
      .andExpect(status().isConflict())
      .andDo(document("group/rename/exists"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  void group_get_single_ok() {

    final var groupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    mockMvc.perform(get("/group/" + groupId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name", equalTo("group_1")))
      .andExpect(jsonPath("$.parentId", nullValue()))
      .andDo(document("group/get/single-ok"));
  }

  @Test
  @SneakyThrows
  void group_get_single_not_exists() {

    mockMvc.perform(get("/group/123"))
      .andExpect(status().isNotFound())
      .andDo(document("group/get/single-not-exists"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  void group_get_list() {

    final var groupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    mockMvc.perform(get("/group"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.[0].name", equalTo("group_1")))
      .andExpect(jsonPath("$.[0].parentId").doesNotExist())
      .andExpect(jsonPath("$.[1].name", equalTo("group_2")))
      .andExpect(jsonPath("$.[1].parentId", equalTo(groupId)))
      .andDo(document("group/get/pagination"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  void group_delete_recursively() {

    final var group1Id = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    final var group2Id = keycloakGroupRepository.findByName("group_2").orElseThrow().getId();

    mockMvc.perform(delete("/group/{groupId}", group1Id))
      .andExpect(status().isOk())
      .andDo(document("group/delete/recursively"));

    mockMvc.perform(get("/group/{groupId}", group1Id))
      .andExpect(status().isNotFound());
    mockMvc.perform(get("/group/{groupId}", group2Id))
      .andExpect(status().isNotFound());
  }
}