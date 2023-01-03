package com.muchencute.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Calendar;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"type", "date", "targetid", "targettype"})
)
public class BizCount {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private Long ID;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String date;

  @Builder.Default
  @Column(nullable = false)
  private Long count = 1L;

  @Embedded
  private BizLogTarget target;

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
    BizCount v = (BizCount) o;
    return ID != null && Objects.equals(ID, v.ID);
  }

  @Override
  public int hashCode() {

    return getClass().hashCode();
  }
}
