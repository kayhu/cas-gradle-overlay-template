package org.iakuh.kauth.cas.dao.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tenant")
public class Tenant extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "code", unique = true, nullable = false)
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

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
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
}
