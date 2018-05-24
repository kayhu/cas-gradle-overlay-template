package org.iakuh.kauth.cas.dao.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "domain", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "code"})})
public class Domain extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "tenant_id", nullable = false)
  private Long tenantId;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "public_key", length = 2048)
  private String publicKey;

  @Column(name = "private_key", length = 2048)
  private String privateKey;

  @Column(name = "disabled", nullable = false)
  private Boolean disabled = Boolean.FALSE;

  @Column(name = "create_time", nullable = false)
  private ZonedDateTime createTime;

  @Column(name = "update_time", nullable = false)
  private ZonedDateTime updateTime;

  @ManyToOne
  @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
  private Tenant tenant;

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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
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
}
