package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.KeycloakGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeycloakGroupRepository extends JpaRepository<KeycloakGroup, String> {

  List<KeycloakGroup> findAllByParentGroup(String parentGroup);
}