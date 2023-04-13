package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserAttributeRepository extends JpaRepository<UserAttribute, String> {

  @Transactional(readOnly = true)
  Optional<UserAttribute> findByUserEntity_IdAndName(String id, String name);
}