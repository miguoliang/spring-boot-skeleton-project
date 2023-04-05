package com.muchencute.test.environment;

import com.muchencute.test.environment.listener.MyTestExecutionListener;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

  @RegisterExtension
  final RestDocumentationExtension restDocumentation = new RestDocumentationExtension();

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

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
             RestDocumentationContextProvider restDocumentationContextProvider) {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
      .apply(documentationConfiguration(restDocumentationContextProvider))
      .alwaysDo(print())
      .alwaysDo(document("{method-name}",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint())))
      .build();
  }
}
