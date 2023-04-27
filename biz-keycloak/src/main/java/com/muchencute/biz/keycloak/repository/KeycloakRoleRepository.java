package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.KeycloakRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface KeycloakRoleRepository extends JpaRepository<KeycloakRole, String> {

  boolean existsByNameAndClientRole(String roleName, Boolean clientRole);

  Set<KeycloakRole> findByNameInAndRealmIdAndClientAndClientRoleIsTrue(Set<String> name, String realmId, String client);

  Set<KeycloakRole> findByUsers_IdAndClientRoleTrueAndClientAndRealmId(String id, String client, String realmId);

}