package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.Set;

public interface UserEntityRepository extends JpaRepository<UserEntity, String>,
  JpaSpecificationExecutor<UserEntity> {

  boolean existsByUsername(String username);

  Optional<UserEntity> findByUsernameAndRealmId(String username, String realmId);

  Set<UserEntity> findAllByUsernameInAndRealmId(Set<String> protectedUsernames, String realmId);
}