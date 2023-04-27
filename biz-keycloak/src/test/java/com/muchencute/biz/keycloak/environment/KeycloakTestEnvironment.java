package com.muchencute.biz.keycloak.environment;

import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.keycloak.EnableKeycloakIntegration;
import com.muchencute.biz.keycloak.advisor.ControllerExceptionHandler;
import com.muchencute.biz.keycloak.environment.listener.MyTestExecutionListener;
import com.muchencute.biz.keycloak.environment.service.KeycloakAccessTokenService;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.repository.KeycloakRoleRepository;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@TestPropertySource(properties = {
  "spring.liquibase.enabled=false",
})
@ExtendWith(SpringExtension.class)
@TestExecutionListeners(
  value = MyTestExecutionListener.class,
  mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {
  ControllerExceptionHandler.class,
  KeycloakAccessTokenService.class,
  KeycloakClientService.class,
  KeycloakGroupRepository.class,
  KeycloakGroupService.class,
  KeycloakRoleRepository.class,
  KeycloakRoleService.class,
  KeycloakService.class,
  KeycloakUserService.class,
  UserEntityRepository.class,
  KeycloakTestEnvironment.TestConfig.class
})
@WebMvcTest
@ActiveProfiles("test")
@Testcontainers
public abstract class KeycloakTestEnvironment {

  private final static String PGSQL_ROOT_USER = "root";
  private final static String PGSQL_ROOT_PASSWORD = "example";
  public final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    DockerImageName.parse("postgres:15-alpine3.17"))
    .withUsername(PGSQL_ROOT_USER)
    .withPassword(PGSQL_ROOT_PASSWORD)
    .withClasspathResourceMapping("database.sql", "/docker-entrypoint-initdb.d/databases.sql",
      BindMode.READ_ONLY)
    .withReuse(true);
  public final static GenericContainer<?> keycloak = new GenericContainer<>(
    DockerImageName.parse("bitnami/keycloak:20.0.5-debian-11-r13"))
    .withEnv("KEYCLOAK_CREATE_ADMIN_USER", "true")
    .withEnv("KEYCLOAK_ADMIN_USER", "admin")
    .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
    .withEnv("KEYCLOAK_DATABASE_NAME", "keycloak")
    .withEnv("KEYCLOAK_DATABASE_HOST", "postgres")
    .withEnv("KEYCLOAK_DATABASE_USER", PGSQL_ROOT_USER)
    .withEnv("KEYCLOAK_DATABASE_PASSWORD", PGSQL_ROOT_PASSWORD)
    .withEnv("KEYCLOAK_PROXY", "edge")
    .withEnv("KEYCLOAK_HTTP_RELATIVE_PATH", "/auth")
    .withExposedPorts(8080)
    .withLogConsumer(new Slf4jLogConsumer(log))
    .dependsOn(postgres)
    .withReuse(true);
  private static String keycloakAuthServerUrl;
  @RegisterExtension
  final RestDocumentationExtension restDocumentation = new RestDocumentationExtension();
  protected MockMvc mockMvc;
  @Autowired
  protected KeycloakService keycloakService;

  @DynamicPropertySource
  @SneakyThrows
  static void bindProperties(DynamicPropertyRegistry registry) {

    final var network = Network.newNetwork();
    postgres.withNetwork(network).withNetworkAliases("postgres").start();
    final var keycloakJdbcUrl = postgres.getJdbcUrl().replace("test", "keycloak");
    keycloak.withNetwork(network).withNetworkAliases("keycloak").start();

    if (StrUtil.isBlank(keycloakAuthServerUrl)) {
      keycloakAuthServerUrl = String.format("http://%s:%d/auth",
        keycloak.getHost(), keycloak.getMappedPort(8080));
      initKeycloakRealmSettings(keycloakAuthServerUrl);
    }

    registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
      () -> String.format("%s/realms/app/protocol/openid-connect/certs", keycloakAuthServerUrl));
    registry.add("app.keycloak.auth-server-url", () -> keycloakAuthServerUrl);
    registry.add("app.keycloak.realm", () -> "app");
    registry.add("app.keycloak.client-id", () -> "console-cli");
    registry.add("app.keycloak.username", () -> "admin");
    registry.add("app.keycloak.password", () -> "admin");
    registry.add("app.keycloak.datasource.url", () -> keycloakJdbcUrl);
    registry.add("app.keycloak.datasource.jdbcUrl", () -> keycloakJdbcUrl);
    registry.add("app.keycloak.datasource.username", () -> "root");
    registry.add("app.keycloak.datasource.password", () -> "example");
    registry.add("app.keycloak.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("app.keycloak.datasource.dialect",
      () -> "org.hibernate.dialect.PostgreSQLDialect");
  }

  static void initKeycloakRealmSettings(String url) {

    final var keycloak = org.keycloak.admin.client.Keycloak.getInstance(
      url, "master", "admin", "admin", "admin-cli");

    final var realmRepresentation = new RealmRepresentation();
    realmRepresentation.setRealm("app");
    realmRepresentation.setEnabled(true);
    keycloak.realms().create(realmRepresentation);

    final var realmResource = keycloak.realm("app");
    log.info("Realm id: {}", realmResource.toRepresentation().getId());

    final var clientRepresentation = new ClientRepresentation();
    clientRepresentation.setClientId("console-cli");
    clientRepresentation.setProtocol("openid-connect");
    clientRepresentation.setDirectAccessGrantsEnabled(true);
    realmResource.clients().create(clientRepresentation).close();
  }

  @BeforeEach
  void beforeEach(WebApplicationContext webApplicationContext,
                  RestDocumentationContextProvider restDocumentationContextProvider) {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
      .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider)).alwaysDo(MockMvcResultHandlers.print())
      .alwaysDo(MockMvcRestDocumentation.document("{method-name}", Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
        Preprocessors.preprocessResponse(Preprocessors.prettyPrint())))
      .build();
  }

  @AfterEach
  void afterEach() {

    keycloakService.getUsersResource().list().forEach(it ->
      keycloakService.getUserResourceById(it.getId()).remove());

    keycloakService.getClientResource().roles().list().forEach(it ->
      keycloakService.getClientRoleResource(it.getName()).remove());

    keycloakService.getGroupsResource().groups().forEach(it ->
      keycloakService.getGroupResource(it.getId()).remove());

    keycloakService.getRealmResource().clientScopes().findAll().forEach(it ->
      keycloakService.getClientScopeResource(it.getName()).remove());

    keycloakService.getRealmResource().logoutAll();
  }

  @EnableKeycloakIntegration
  public static class TestConfig {

  }
}
