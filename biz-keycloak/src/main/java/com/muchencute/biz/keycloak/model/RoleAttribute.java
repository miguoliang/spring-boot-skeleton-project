package com.muchencute.biz.keycloak.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.ToString.Exclude;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "ROLE_ATTRIBUTE")
public class RoleAttribute {

  @Id
  @Column(name = "ID", nullable = false, length = 36)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ROLE_ID", nullable = false)
  @Exclude
  private KeycloakRole role;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "VALUE")
  private String value;

}