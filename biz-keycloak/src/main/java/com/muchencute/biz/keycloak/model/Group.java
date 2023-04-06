package com.muchencute.biz.keycloak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

  private String id;

  private String name;

  private String parentId;

  // 数据库中直接读取的根节点的 parentId 是空格，
  // 所以在输出的时候转换成 null，使全局语义统一。
  public String getParentId() {
    return " ".equals(parentId) ? null : parentId;
  }
}
