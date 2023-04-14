package com.muchencute.biz.keycloak.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "KEYCLOAK_GROUP")
public class KeycloakGroup {

  @Id
  @Column(name = "ID", nullable = false, length = 36)
  private String id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "PARENT_GROUP", nullable = false, length = 36)
  private String parentGroup;

  @Column(name = "REALM_ID", length = 36)
  private String realmId;

  @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<UserEntity> users;

}