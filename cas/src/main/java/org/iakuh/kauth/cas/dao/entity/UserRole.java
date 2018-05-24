package org.iakuh.kauth.cas.dao.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "ref_user_role", indexes = {
    @Index(columnList = "user_id"),
    @Index(columnList = "role_id")
})
public class UserRole implements Serializable {

  private static final long serialVersionUID = -7975912252702014256L;
  @Id
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Id
  @Column(name = "role_id", nullable = false)
  private Long roleId;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }
}
