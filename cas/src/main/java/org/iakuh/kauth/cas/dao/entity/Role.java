package org.iakuh.kauth.cas.dao.entity;

import java.time.ZonedDateTime;
import javax.persistence.CascadeType;
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
@Table(name = "role", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"domain_id", "code"})})
public class Role extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "domain_id", nullable = false)
  private Long domainId;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "disabled", nullable = false)
  private Boolean disabled = Boolean.FALSE;

  @Column(name = "create_time", nullable = false)
  private ZonedDateTime createTime;

  @Column(name = "update_time", nullable = false)
  private ZonedDateTime updateTime;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "domain_id", insertable = false, updatable = false)
  private Domain domain;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public Long getDomainId() {
    return domainId;
  }

  public void setDomainId(Long domainId) {
    this.domainId = domainId;
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

  public Domain getDomain() {
    return domain;
  }

  public void setDomain(Domain domain) {
    this.domain = domain;
  }
}
