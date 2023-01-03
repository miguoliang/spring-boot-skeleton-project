package com.muchencute.biz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Calendar;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
@Table(indexes = {
    @Index(columnList = "username"),
    @Index(columnList = "targetId, targetType")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username", "targetId", "targetType"})
})
public class Favorite {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private Long ID;

  @Column(nullable = false)
  @JsonIgnore
  private String username;

  @Embedded
  private BizLogTarget target;

  @Transient
  private String logContent;

  @Transient
  private Calendar logCreatedAt;

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Favorite v = (Favorite) o;
    return ID != null && Objects.equals(ID, v.ID);
  }

  @Override
  public int hashCode() {

    return getClass().hashCode();
  }
}
