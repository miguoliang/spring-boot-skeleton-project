package com.muchencute.biz.keycloak.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.muchencute.biz.keycloak.helper.JwtHelper;
import com.muchencute.biz.keycloak.model.*;
import com.muchencute.biz.keycloak.repository.EventEntityRepository;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.request.NewUserRequest;
import com.muchencute.biz.keycloak.request.RegisterUserRequest;
import com.muchencute.biz.keycloak.request.UpdateUserRequest;
import jakarta.persistence.criteria.JoinType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional(transactionManager = "keycloakTransactionManager")
public class KeycloakUserService {

  private final EventEntityRepository eventEntityRepository;

  private final KeycloakService keycloakService;

  private final UserEntityRepository userEntityRepository;

  @Autowired
  public KeycloakUserService(KeycloakService keycloakService,
                             UserEntityRepository userEntityRepository,
                             EventEntityRepository eventEntityRepository) {

    this.keycloakService = keycloakService;
    this.userEntityRepository = userEntityRepository;
    this.eventEntityRepository = eventEntityRepository;
  }

  private static void setCredential(String password, UserRepresentation ur) {

    final var cr = new CredentialRepresentation();
    cr.setType("password");
    cr.setValue(password);
    cr.setTemporary(false);
    ur.setCredentials(List.of(cr));
  }

  private static Specification<UserEntity> isRealm(String realm) {

    return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(UserEntity_.REALM_ID), realm);
  }

  private static Specification<UserEntity> nameLike(String keyword) {

    return (root, query, criteriaBuilder) ->
            StrUtil.isNotBlank(keyword) ?
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get(UserEntity_.FIRST_NAME), "%" + keyword + "%"),
                            criteriaBuilder.like(root.get(UserEntity_.USERNAME), "%" + keyword + "%")) :
                    criteriaBuilder.and();
  }

  private static Specification<UserEntity> statusIn(Set<String> statusSet) {

    return (root, query, criteriaBuilder) -> {
      if (CollUtil.isEmpty(statusSet)) {
        return criteriaBuilder.and();
      }
      final var attributeJoin = root.<UserEntity, UserAttribute>join("attributes", JoinType.INNER);
      return criteriaBuilder.and(
              criteriaBuilder.equal(attributeJoin.get("name"), "status"),
              attributeJoin.get("value").in(statusSet)
      );
    };
  }

  private static Specification<UserEntity> roleIs(String roleId) {

    return (root, query, criteriaBuilder) -> {
      if (StrUtil.isBlank(roleId)) {
        return criteriaBuilder.and();
      }
      final var roleJoin = root.<UserEntity, KeycloakRole>join("roles", JoinType.INNER);
      return criteriaBuilder.equal(roleJoin.get("id"), roleId);
    };
  }

  private static Specification<UserEntity> groupIn(Set<String> groupIds) {

    return (root, query, criteriaBuilder) -> {
      if (CollUtil.isEmpty(groupIds)) {
        return criteriaBuilder.and();
      }
      final var groupJoin = root.<UserEntity, KeycloakGroup>join("groups", JoinType.INNER);
      return groupJoin.get("id").in(groupIds);
    };
  }

  private Specification<UserEntity> userNameNotStart() {

    return (root, query, criteriaBuilder) ->
            criteriaBuilder.notLike(root.get(UserEntity_.USERNAME), "reserved_%");
  }

  public User registerUser(RegisterUserRequest request) {

    final var ur = new UserRepresentation();
    ur.setUsername(request.getUsername());
    ur.setFirstName(request.getName());
    ur.setEnabled(true);
    ur.singleAttribute("phoneNumber", request.getPhoneNumber());
    ur.singleAttribute("picture", request.getPicture());
    ur.singleAttribute("status", "pending");

    setCredential(request.getPassword(), ur);

    final var userResource = keycloakService.newUserResource(ur);
    if (StrUtil.isNotBlank(request.getGroupId())) {
      userResource.joinGroup(request.getGroupId());
    }

    return getUserResponse(userResource.toRepresentation().getId());
  }

  public User newUser(NewUserRequest request) {

    final var ur = new UserRepresentation();
    ur.setUsername(request.getUsername());
    ur.setFirstName(request.getName());
    ur.setEnabled(true);
    ur.singleAttribute("phoneNumber", request.getPhoneNumber());
    ur.singleAttribute("picture", request.getPicture());

    setCredential(request.getPassword(), ur);

    final var userResource = keycloakService.newUserResource(ur);
    if (StrUtil.isNotBlank(request.getGroupId())) {
      userResource.joinGroup(request.getGroupId());
    }

    final var userRepresentation = userResource.toRepresentation();
    if (StrUtil.isNotBlank(request.getRoleId())) {
      final var roleRepresentation = keycloakService.getRealmResource().rolesById()
              .getRole(request.getRoleId());
      keycloakService.attachRoleResource(userRepresentation.getUsername(), roleRepresentation);
      keycloakService.acceptUser(userRepresentation.getId());
    }

    return getUserResponse(userRepresentation.getId());
  }

  private User getUserResponse(UserEntity user) {

    final var builder = User.builder()
            .id(user.getId())
            .username(user.getUsername())
            .enabled(user.getEnabled())
            .phoneNumber(user
                    .getAttributes()
                    .stream()
                    .filter(it -> "phoneNumber".equals(it.getName()))
                    .findFirst().orElseGet(UserAttribute::new)
                    .getValue())
            .name(user.getFirstName())
            .createdAt(user.getCreatedTimestamp());

    if (CollUtil.isNotEmpty(user.getRoles())) {
      user.getRoles()
              .stream()
              .filter(KeycloakRole::getClientRole)
              .findFirst()
              .ifPresent(role -> builder.role(Role.builder()
                      .id(role.getId())
                      .name(role.getName())
                      .build()));
    }

    if (CollUtil.isNotEmpty(user.getGroups())) {
      final var group = user.getGroups().stream().findFirst().orElseThrow();
      builder.group(Group.builder()
              .id(group.getId())
              .name(group.getName())
              .build());
    }

    eventEntityRepository.countByIpAddress(user.getId()).stream().findFirst().ifPresent(it ->
            builder.commonIp(it[0].toString()));

    eventEntityRepository.findFirstByUserIdAndTypeIsOrderByEventTimeDesc(user.getId(), "LOGIN")
            .ifPresent(it -> {
              builder.lastLoginIp(it.getIpAddress());
              builder.lastLoginTime(it.getEventTime());
            });

    return builder.build();
  }

  private User getUserResponse(String id) {

    final var user = userEntityRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return getUserResponse(user);
  }

  public Page<User> getUsers(String roleId, Set<String> groupIds, String keyword,
                             Set<String> statusSet,
                             Pageable pageable) {

    final var users = userEntityRepository.findAll(
            Specification
                    .where(nameLike(keyword))
                    .and(isRealm(keycloakService.getRealm()))
                    .and(statusIn(statusSet))
                    .and(roleIs(roleId))
                    .and(groupIn(groupIds))
                    .and(userNameNotStart()), pageable);

    final var content = users.getContent().stream().map(this::getUserResponse).toList();
    return new PageImpl<>(content, pageable, users.getTotalElements());
  }

  public void resetUserCredentialById(String id) {

    final var userResource = keycloakService.getUserResourceById(id);
    final var userRepresentation = userResource.toRepresentation();
    userRepresentation.setEnabled(true);
    userRepresentation.singleAttribute("status", "normal");
    userResource.update(userRepresentation);
    Optional.ofNullable(userRepresentation.getCredentials())
            .ifPresent(credentialRepresentations ->
                    credentialRepresentations.forEach(it -> userResource.removeCredential(it.getId())));

    final var cr = new CredentialRepresentation();
    cr.setType("password");
    cr.setValue("Aa123456.");
    cr.setTemporary(false);
    userResource.credentials().add(cr);
  }

  public User updateUser(String id, UpdateUserRequest request) {

    final var userResource = keycloakService.getUserResourceById(id);
    final var userRepresentation = userResource.toRepresentation();
    userRepresentation.setFirstName(request.getName());
    userRepresentation.singleAttribute("picture", request.getPicture());
    userRepresentation.singleAttribute("phoneNumber", request.getPhoneNumber());
    userResource.update(userRepresentation);

    userResource.groups().forEach(it -> userResource.leaveGroup(it.getId()));
    if (StrUtil.isNotBlank(request.getGroupId())) {
      userResource.joinGroup(request.getGroupId());
    }

    keycloakService.detachAllRoleResource(userRepresentation.getUsername());
    if (StrUtil.isNotBlank(request.getRoleId())) {
      final var roleRepresentation = keycloakService.getRealmResource().rolesById()
              .getRole(request.getRoleId());
      keycloakService.attachRoleResource(userRepresentation.getUsername(), roleRepresentation);
    }

    return getUserResponse(userResource.toRepresentation().getId());
  }

  public User getUser(String id) {

    final var userRepresentation = keycloakService.getUserResourceById(id).toRepresentation();
    return getUserResponse(userRepresentation.getId());
  }

  public void resetUserCredentialByAnonymous(String username) {

    final var userResource = keycloakService.getUserResource(username);
    final var userRepresentation = userResource.toRepresentation();
    userRepresentation.setEnabled(false);
    userRepresentation.singleAttribute("status", "reset-password");
    userResource.update(userRepresentation);
    Optional.ofNullable(userRepresentation.getCredentials())
            .ifPresent(credentialRepresentations ->
                    credentialRepresentations.forEach(it -> userResource.removeCredential(it.getId())));
  }

  @SneakyThrows
  public User updateProfile(User user) {

    final var username = JwtHelper.getUsername();
    final var userEntity = userEntityRepository.findByUsernameAndRealmId(username,
                    keycloakService.getRealm())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "当前用户不存在！"));
    if (!userEntity.getEnabled()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前用户已被禁用！");
    }
    final var userResource = keycloakService.getUserResourceById(userEntity.getId());
    final var userRepresentation = userResource.toRepresentation();
    userRepresentation.singleAttribute("phoneNumber", user.getPhoneNumber());
    userRepresentation.singleAttribute("picture", user.getPicture());
    userRepresentation.setFirstName(user.getName());
    userResource.update(userRepresentation);
    return getUser(userEntity.getId());
  }

  public void resetUserCredential(String username, String currentPassword, String password) {

    checkUsernameAndPassword(username, currentPassword);

    final var userResource = keycloakService.getUserResource(username);
    final var userRepresentation = userResource.toRepresentation();
    Optional.ofNullable(userRepresentation.getCredentials()).ifPresent(
            credential -> credential.forEach(it -> userResource.removeCredential(it.getId())));
    final var credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setValue(password);
    credentialRepresentation.setType("password");
    credentialRepresentation.setTemporary(false);
    userRepresentation.setCredentials(List.of(credentialRepresentation));
    userResource.update(userRepresentation);
  }

  private void checkUsernameAndPassword(String username, String password) {

    final var tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
            keycloakService.getAuthServerUrl(),
            keycloakService.getRealm());
    // 因为 console-cli 出于安全考虑，禁止了通过 API 直接获取 token，所以要验证原密码是否正确，需要借道
    // 不支持页面登录的 client，此处借用的是专为模型调用的 model-cli
    final var jsonObject = WebClient.create(tokenUrl)
            .post()
            .body(BodyInserters.fromFormData("client_id", "model-cli")
                    .with("username", username)
                    .with("password", password)
                    .with("client_secret", "22ISi1NmKgkpUm3xJjdqvURIafg2ZLpx")
                    .with("grant_type", "password"))
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(clientResponse -> clientResponse.statusCode().equals(HttpStatus.OK)
                    ? clientResponse.bodyToMono(JSONObject.class) : Mono.empty())
            .block();

    if (jsonObject == null) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "原密码错误！");
    }

    final var sessionState = jsonObject.get("session_state", String.class);
    keycloakService.deleteSession(sessionState);
  }

  public void deleteUserById(String id) {

    keycloakService.getUserResourceById(id).remove();
  }
}
