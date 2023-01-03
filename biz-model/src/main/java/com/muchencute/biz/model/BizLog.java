package com.muchencute.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Calendar;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
public class BizLog {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private Long ID;

  @Column(nullable = false)
  private String module;

  @Column(name = "type_", nullable = false)
  private String type;

  @Embedded
  private BizLogTarget target;

  @Column(columnDefinition = "text")
  private String content;

  @Column(nullable = false)
  private String ip;

  @Column(nullable = false)
  private String username;

  @Column
  private String userRole;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Calendar createdAt;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Calendar updatedAt;

  @Transient
  private boolean favorite;

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    BizLog v = (BizLog) o;
    return ID != null && Objects.equals(ID, v.ID);
  }

  @Override
  public int hashCode() {

    return getClass().hashCode();
  }
}
