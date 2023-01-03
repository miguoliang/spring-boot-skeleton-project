package com.muchencute.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.ToString.Exclude;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private Long ID;

  @Column
  private String title;

  @Column(columnDefinition = "text")
  private String content;

  @Column
  private String userId;

  @Builder.Default
  @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Exclude
  private Set<NotificationRead> reads = new HashSet<>();

  @Transient
  private Boolean read;

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
    Notification v = (Notification) o;
    return ID != null && Objects.equals(ID, v.ID);
  }

  @Override
  public int hashCode() {

    return getClass().hashCode();
  }
}
