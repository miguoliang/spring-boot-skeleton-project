package com.muchencute.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class WorkOrder {

  public final static String COMPLETED = "已完成";

  public final static String PROCESSING = "采集中";

  public final static String PENDING = "待下发";

  public final static List<String> LETTERS = List.of("T", "U", "V", "W", "X", "Y", "Z");

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private Long ID;

  @Column(nullable = false)
  private String name;

  @ElementCollection(fetch = FetchType.LAZY)
  private Set<String> keywords;

  @Column(columnDefinition = "text")
  private String description;

  @Column
  private String type;

  @Column(nullable = false)
  private String creator;

  @Column(nullable = false)
  private String status = PENDING;

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
    WorkOrder v = (WorkOrder) o;
    return ID != null && Objects.equals(ID, v.ID);
  }

  @Override
  public int hashCode() {

    return getClass().hashCode();
  }
}