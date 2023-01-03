package com.muchencute.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
@Builder
public class BizLogTarget {

  @Column
  @JsonProperty(access = Access.READ_ONLY)
  private String targetName;

  @Column
  private String targetType;

  @Builder.Default
  @Column(nullable = false)
  private String targetId = "";
}
