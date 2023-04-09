package com.muchencute.test.environment;

import com.muchencute.biz.keycloak.model.KeycloakRole;
import com.muchencute.biz.keycloak.model.UserEntity;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.service.KeycloakService;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;
import java.util.Set;

public class UnitTestEnvironment extends TestEnvironment {

  @MockBean
  private UserEntityRepository userEntityRepository;

  @MockBean
  private KeycloakService keycloakService;

  @MockBean
  @SuppressWarnings("unused")
  private MinioClient minioClient;

  @DynamicPropertySource
  @SneakyThrows
  static void bindProperties(DynamicPropertyRegistry registry) {
    // 单元测试不依赖于外部环境，所以此处禁用 Zookeeper
    registry.add("spring.cloud.zookeeper.enabled", () -> false);
  }

  @BeforeAll
  void beforeAll() {

    // BizLogger 会记录用户信息，因此要 mock 一个固定的返回值，否则不会生成 BizLog 记录，
    // 若后续测试有具体的值需求或者判定逻辑，那么需要针对每个测试的具体逻辑，mock 具体的值来进行测试，
    // 届时，可以考虑将 mock 返回值的代码写到对应的 method 中，而不是 beforeAll。
    final var userEntity = new UserEntity();
    userEntity.setId("admin");
    userEntity.setUsername("admin");
    userEntity.setFirstName("admin");
    userEntity.setRoles(Set.of(KeycloakRole.builder().name("超级管理员").clientRole(true).build()));
    Mockito.when(keycloakService.getRealm()).thenReturn("master");
    Mockito.when(userEntityRepository.findByUsernameAndRealmId("admin", keycloakService.getRealm()))
      .thenReturn(Optional.of(userEntity));
  }
}
