package com.muchencute.biz.keycloak.misc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CustomFeatureTests.TestConfig.class)
public class CustomFeatureTests {

  @Autowired
  ApplicationContext applicationContext;

  @Test
  public void a_foo_bean_should_exists() {

    Assertions.assertFalse(applicationContext.getBeansOfType(Foo.class).isEmpty());
  }

  @EnableCustomFeature(value = "foo")
  static class TestConfig {

  }
}
