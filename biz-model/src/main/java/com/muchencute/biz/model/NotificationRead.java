package com.muchencute.biz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Calendar;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class NotificationRead {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private Long ID;

  @Column(nullable = false)
  private String userId;

  @ManyToOne
  @JsonIgnore
  private Notification notification;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Calendar createdAt;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Calendar updatedAt;

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    NotificationRead v = (NotificationRead) o;
    return ID != null && Objects.equals(ID, v.ID);
  }

  @Override
  public int hashCode() {

    return getClass().hashCode();
  }
}