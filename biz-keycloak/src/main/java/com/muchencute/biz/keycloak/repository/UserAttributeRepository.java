package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAttributeRepository extends JpaRepository<UserAttribute, String> {

  Optional<UserAttribute> findByUserEntity_IdAndName(String id, String name);
}