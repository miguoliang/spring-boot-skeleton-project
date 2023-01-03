package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.KeycloakRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeycloakRoleRepository extends JpaRepository<KeycloakRole, String> {

  boolean existsByNameAndClientRole(String roleName, Boolean clientRole);
}