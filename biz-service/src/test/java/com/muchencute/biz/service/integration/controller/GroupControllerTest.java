package com.muchencute.biz.service.integration.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchencute.biz.keycloak.model.Group;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.request.MoveGroupRequest;
import com.muchencute.biz.keycloak.request.RenameGroupRequest;
import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import com.muchencute.test.environment.KeycloakTestEnvironment;
import com.muchencute.test.environment.mock.MockGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WithMockUser(username = "admin", authorities = "group:crud")
class GroupControllerTest extends KeycloakTestEnvironment {

  @Autowired
  private KeycloakGroupService keycloakGroupService;

  @Autowired
  private KeycloakGroupRepository keycloakGroupRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1", parent = " ")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_3", parent = "group_2")
  void move_a_group_to_root() {

    final var groupToMove = keycloakGroupRepository.findByName("group_3")
      .stream().findFirst().orElseThrow().getId();
    mockMvc.perform(post("/group/" + groupToMove + ":move")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isOk())
      .andDo(document("group/move/move_a_group_to_root"));

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
  @MockGroup(name = "group_1", parent = " ")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_a", parent = " ")
  @MockGroup(name = "group_b", parent = "group_a")
  void move_a_group_to_another_group() {

    final var groupToMove = keycloakGroupRepository.findByName("group_1").orElseThrow();
    final var targetGroup = keycloakGroupRepository.findByName("group_b").orElseThrow();
    final var moveRequest = new MoveGroupRequest();
    moveRequest.setParentId(targetGroup.getId());
    mockMvc.perform(post("/group/" + groupToMove.getId() + ":move")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(moveRequest)))
      .andExpect(status().isOk())
      .andDo(document("group/move/move_a_group_to_another_group"));

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
  void group_exists() {

    mockMvc.perform(head("/group")
        .param("name", "group_1"))
      .andExpect(status().isOk())
      .andDo(document("group/stat/exists"));
  }

  @Test
  @SneakyThrows
  void group_not_exists() {
    mockMvc.perform(head("/group")
        .param("name", "group_1"))
      .andExpect(status().isNotFound())
      .andDo(document("group/stat/not_exists"));
  }

  @Test
  @SneakyThrows
  void new_root_group() {

    final var newGroup = new Group();
    newGroup.setName("group_1");

    final var responseText = mockMvc.perform(post("/group")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newGroup)))
      .andExpect(status().isCreated())
      .andDo(document("group/new/new_root_group"))
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
  void new_child_group() {

    final var newGroup = new Group();
    newGroup.setName("group_child");
    final var rootGroupId = keycloakGroupRepository.findByName("group_root").orElseThrow().getId();
    newGroup.setParentId(rootGroupId);

    final var responseText = mockMvc.perform(post("/group")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newGroup)))
      .andExpect(status().isCreated())
      .andDo(document("group/new/new_child_group"))
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
  void rename_a_group() {

    final var renameGroup = new RenameGroupRequest();
    renameGroup.setNewGroupName("group_2");

    final var groupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    mockMvc.perform(post("/group/" + groupId + ":rename")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(renameGroup)))
      .andExpect(status().isOk())
      .andDo(document("group/rename"));

    mockMvc.perform(get("/group/" + groupId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name", equalTo("group_2")));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  void retrieve_a_group() {

    final var groupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    mockMvc.perform(get("/group/" + groupId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name", equalTo("group_1")))
      .andExpect(jsonPath("$.parentId", nullValue()))
      .andDo(document("group/get"));
  }

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1")
  @MockGroup(name = "group_2", parent = "group_1")
  void retrieve_all_groups() {

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
  void delete_a_group() {

    final var groupId = keycloakGroupRepository.findByName("group_1").orElseThrow().getId();
    mockMvc.perform(delete("/group/" + groupId))
      .andExpect(status().isOk())
      .andDo(document("group/delete"));

    mockMvc.perform(get("/group/" + groupId))
      .andExpect(status().isNotFound());
  }
}