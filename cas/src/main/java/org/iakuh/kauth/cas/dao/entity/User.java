package org.iakuh.kauth.cas.dao.entity;

import java.time.ZonedDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "username"})})
public class User extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "tenant_id", nullable = false)
  private Long tenantId;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "disabled", nullable = false)
  private Boolean disabled = Boolean.FALSE;

  @Column(name = "create_time", nullable = false)
  private ZonedDateTime createTime;

  @Column(name = "update_time", nullable = false)
  private ZonedDateTime updateTime;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
  private Tenant tenant;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "ref_user_role",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
  private Set<Role> roles;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public Long getTenantId() {
    return tenantId;
  }

  public void setTenantId(Long tenantId) {
    this.tenantId = tenantId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getDisabled() {
    return disabled;
  }

  public void setDisabled(Boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  public ZonedDateTime getCreateTime() {
    return createTime;
  }

  @Override
  public void setCreateTime(ZonedDateTime createTime) {
    this.createTime = createTime;
  }

  @Override
  public ZonedDateTime getUpdateTime() {
    return updateTime;
  }

  @Override
  public void setUpdateTime(ZonedDateTime updateTime) {
    this.updateTime = updateTime;
  }

  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}