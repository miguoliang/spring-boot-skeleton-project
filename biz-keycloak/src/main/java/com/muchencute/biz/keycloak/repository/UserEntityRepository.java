package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, String>,
        JpaSpecificationExecutor<UserEntity> {

  boolean existsByUsername(String username);

  Optional<UserEntity> findByUsernameAndRealmId(String username, String realmId);
}