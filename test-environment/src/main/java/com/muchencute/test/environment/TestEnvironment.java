package com.muchencute.test.environment;

import com.muchencute.test.environment.listener.MyTestExecutionListener;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

/**
 * API 测试基类。 该类对于 Keycloak 和 Minio 系统的相关 Bean 进行了 Mock 处理。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
  "spring.profiles.active=test",
  "logging.level.liquibase=debug",
})
@Slf4j
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestInstance(Lifecycle.PER_CLASS)
// 此处要设置 mergeMode，否则回替换调原来的 listeners，会导致部分测试失败
// 参考文献 https://www.baeldung.com/spring-testexecutionlistener
@TestExecutionListeners(value = MyTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@Rollback(false)
abstract class TestEnvironment {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  @Qualifier("bizEntityManager")
  protected EntityManager bizEntityManager;

  @Autowired
  @Qualifier("keycloakEntityManager")
  protected EntityManager keycloakEntityManager;

  @Autowired
  @Qualifier("bizDataSource")
  protected DataSource bizDataSource;
}
