package com.muchencute.biz.keycloak.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "USER_ATTRIBUTE")
public class UserAttribute {

  @Id
  @Column(name = "ID", nullable = false, length = 36)
  private String id;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "VALUE")
  private String value;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private UserEntity userEntity;
}