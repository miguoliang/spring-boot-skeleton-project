package com.muchencute.biz.keycloak.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ObjectMapperTests {

  ObjectMapper objectMapper = new ObjectMapper();

  // 一个字符串，序列化后，会被双引号包裹，同时也是个合法的 JSON 字符串。
  @Test
  @SneakyThrows
  void object_mapper_write_value_as_string_by_string() {

    final var result = objectMapper.writeValueAsString("hello, world!");
    log.info("result: {}", result);
    Assertions.assertEquals("\"hello, world!\"", result);
  }
}
