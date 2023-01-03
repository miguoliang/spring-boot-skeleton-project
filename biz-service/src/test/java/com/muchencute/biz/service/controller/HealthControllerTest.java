package com.muchencute.biz.service.controller;

import com.muchencute.test.environment.RestUnitTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest extends RestUnitTest {

  @SneakyThrows
  @Test
  void getHealth() {

    mockMvc.perform(get("/health"))
            .andExpect(status().isOk())
            .andDo(document("health"));
  }
}