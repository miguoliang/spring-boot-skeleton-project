package com.muchencute.biz.keycloak.repository;

import com.muchencute.biz.keycloak.model.KeycloakGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface KeycloakGroupRepository extends JpaRepository<KeycloakGroup, String> {

  List<KeycloakGroup> findAllByParentGroup(String parentGroup);

  // 此方法只限单元测试使用，因为实际生产环境中可能存在多个分属不同层级的同名组
  Optional<KeycloakGroup> findByName(String name);

  Optional<KeycloakGroup> findByNameAndParentGroup(String name, String parentGroup);

  Collection<KeycloakGroup> findByUsers_IdAndRealmId(String id, String idOfRealm);
}