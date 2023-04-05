package com.muchencute.biz.service.integration.controller;

import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WithMockUser(username = "admin", authorities = "group:crud")
class GroupControllerTest extends KeycloakTestEnvironment {

  @Autowired
  private KeycloakGroupService keycloakGroupService;

  @Autowired
  private KeycloakGroupRepository keycloakGroupRepository;

  @Test
  @SneakyThrows
  @MockGroup(name = "group_1", parent = " ")
  @MockGroup(name = "group_2", parent = "group_1")
  @MockGroup(name = "group_3", parent = "group_2")
  void move_a_leaf_to_its_root() {

    final var groupToMove = keycloakGroupRepository.findByName("group_3")
      .stream().findFirst().orElseThrow().getId();
    mockMvc.perform(post("/group/" + groupToMove + ":move")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isOk());

    Assertions.assertEquals(2, keycloakGroupService.getGroups()
      .stream().filter(it -> StrUtil.isBlank(it.getParentId())).count());

    mockMvc.perform(get("/group"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()", equalTo(3)))
      .andExpect(jsonPath("$.[?(@.name=='group_3')].parentId", hasSize(1)))
      .andExpect(jsonPath("$.[?(@.name=='group_3')].parentId", contains(" ")));
  }
}