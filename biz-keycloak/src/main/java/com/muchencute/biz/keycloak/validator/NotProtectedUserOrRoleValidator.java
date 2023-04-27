package com.muchencute.biz.keycloak.validator;

import com.muchencute.biz.keycloak.model.KeycloakRole;
import com.muchencute.biz.keycloak.model.UserEntity;
import com.muchencute.biz.keycloak.repository.KeycloakRoleRepository;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.service.KeycloakService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Profiles;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotProtectedUserOrRoleValidator implements
  ConstraintValidator<NotProtectedUserOrRole, Object> {

  private final Set<String> PROTECTED_USERNAMES = new HashSet<>();

  private final Set<String> PROTECTED_USER_IDS = new HashSet<>();

  private final Set<String> PROTECTED_ROLE_NAMES = new HashSet<>();

  private final Set<String> PROTECTED_ROLE_IDS = new HashSet<>();
  private final boolean containsTestProfile;
  private final ApplicationContext applicationContext;
  private KeycloakService keycloakService;
  private UserEntityRepository userEntityRepository;
  private KeycloakRoleRepository keycloakRoleRepository;
  private Set<String> protectedRoleNames;
  private Set<String> protectedUsernames;
  private NotProtectedUserOrRole.FieldType fieldType;
  private NotProtectedUserOrRole.ResourceType resourceType;

  public NotProtectedUserOrRoleValidator(ApplicationContext applicationContext) {

    final var environment = applicationContext.getEnvironment();
    this.containsTestProfile = environment.acceptsProfiles(Profiles.of("test"));
    this.applicationContext = applicationContext;
  }

  private void reloadProtectedUsersAndRoles() {

    final var idOfRealm = keycloakService.getIdOfRealm();
    final var idOfClient = keycloakService.getIdOfClient();

    final var protectedUsers = userEntityRepository
      .findAllByUsernameInAndRealmId(protectedUsernames, idOfRealm);

    final var protectedRoles = keycloakRoleRepository
      .findByNameInAndRealmIdAndClientAndClientRoleIsTrue(
        protectedRoleNames,
        idOfRealm,
        idOfClient);

    PROTECTED_USERNAMES.clear();
    PROTECTED_USER_IDS.clear();
    PROTECTED_ROLE_NAMES.clear();
    PROTECTED_ROLE_IDS.clear();

    PROTECTED_USERNAMES.addAll(protectedUsers.stream().map(UserEntity::getUsername).toList());
    PROTECTED_USER_IDS.addAll(protectedUsers.stream().map(UserEntity::getId).toList());
    PROTECTED_ROLE_NAMES.addAll(protectedRoles.stream().map(KeycloakRole::getName).toList());
    PROTECTED_ROLE_IDS.addAll(protectedRoles.stream().map(KeycloakRole::getId).toList());
  }

  @Override
  public void initialize(NotProtectedUserOrRole constraintAnnotation) {

    ConstraintValidator.super.initialize(constraintAnnotation);
    final var beanNamePrefix = constraintAnnotation.beanNamePrefix();
    final var propertyPrefix = constraintAnnotation.propertyPrefix();
    this.userEntityRepository = applicationContext.getBean(beanNamePrefix + "UserEntityRepository", UserEntityRepository.class);
    this.keycloakRoleRepository = applicationContext.getBean(beanNamePrefix + "KeycloakRoleRepository", KeycloakRoleRepository.class);
    this.keycloakService = applicationContext.getBean(beanNamePrefix + "KeycloakService", KeycloakService.class);
    final var environment = applicationContext.getEnvironment();
    this.protectedRoleNames = Set.of(environment.getProperty(propertyPrefix + ".protected-role-names", String[].class, new String[]{}));
    this.protectedUsernames = Set.of(environment.getProperty(propertyPrefix + ".protected-usernames", String[].class, new String[]{}));
    fieldType = constraintAnnotation.fieldType();
    resourceType = constraintAnnotation.resourceType();

    reloadProtectedUsersAndRoles();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {

    final var items = normalize(value);

    reloadProtectedUsersAndRolesEverytimeInTestProfile();

    if (items.isEmpty()) {
      return true;
    }

    if (fieldType == NotProtectedUserOrRole.FieldType.ID && resourceType == NotProtectedUserOrRole.ResourceType.USER) {
      return items.stream().noneMatch(PROTECTED_USER_IDS::contains);
    } else if (fieldType == NotProtectedUserOrRole.FieldType.NAME && resourceType == NotProtectedUserOrRole.ResourceType.USER) {
      return items.stream().noneMatch(PROTECTED_USERNAMES::contains);
    } else if (fieldType == NotProtectedUserOrRole.FieldType.ID && resourceType == NotProtectedUserOrRole.ResourceType.ROLE) {
      return items.stream().noneMatch(PROTECTED_ROLE_IDS::contains);
    } else if (fieldType == NotProtectedUserOrRole.FieldType.NAME && resourceType == NotProtectedUserOrRole.ResourceType.ROLE) {
      return items.stream().noneMatch(PROTECTED_ROLE_NAMES::contains);
    } else {
      throw new IllegalArgumentException("不支持的类型：" + fieldType.name() + " " + resourceType.name());
    }
  }

  private Collection<String> normalize(Object value) {
    if (value == null) {
      return List.of();
    } else if (value instanceof String string) {
      return List.of(string);
    } else if (value instanceof String[] array) {
      return List.of(array);
    } else if (value instanceof Collection<?> list) {
      for (Object obj : list) {
        if (!(obj instanceof String)) {
          throw new IllegalArgumentException("不支持的类型（集合）：" + value.getClass());
        }
      }
      return list.stream().map(Object::toString).toList();
    } else {
      throw new IllegalArgumentException("不支持的类型：" + value.getClass());
    }
  }

  /**
   * 在测试环境下，每次都重新加载受保护的用户和角色
   * <p>
   * 因为在测试环境下，可能会有多个测试方法，而对应同样一组参数 Hibernate Validator 指挥生成一个对应的 Validator 实例，
   * 并且，由于 Validator 不是一个 Spring Bean，所以无法通过 getBean 方法得到，若要得到需要通过反射的方式，这样就会导致
   * 很复杂的代码去实现一个简单的功能，因此，为了方便测试的进行，在测试环境下每次都会尝试重新读取配置文件列表来重新加载受保护的用户和角色。
   * <p>
   * 另外一个原因是，测试环境下每个方法执行前都会清空并重新创建角色和用户，这会导致 id 的变化，
   * 因此，为了能匹配上对应的名称和 id，所以需要重新初始化对应的常量。
   */
  void reloadProtectedUsersAndRolesEverytimeInTestProfile() {

    if (containsTestProfile) {
      reloadProtectedUsersAndRoles();
    }
  }
}
