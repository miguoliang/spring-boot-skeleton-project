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
@Table(name = "KEYCLOAK_ROLE")
public class KeycloakRole {

  @Id
  @Column(name = "ID", nullable = false, length = 36)
  private String id;

  @Column(name = "CLIENT_REALM_CONSTRAINT")
  private String clientRealmConstraint;

  @Column(name = "CLIENT_ROLE")
  private Boolean clientRole;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "NAME")
  private String name;

  @Column(name = "REALM_ID")
  private String realmId;

  @Column(name = "CLIENT", length = 36)
  private String client;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<UserEntity> users;

}