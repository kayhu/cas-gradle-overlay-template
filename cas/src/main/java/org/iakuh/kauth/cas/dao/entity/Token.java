package org.iakuh.kauth.cas.dao.entity;

import java.time.ZonedDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "token", indexes = @Index(columnList = "token"))
public class Token extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "domain_id", nullable = false)
  private Long domainId;

  @Column(name = "token", nullable = false)
  private String token;

  @Column(name = "aud", nullable = false)
  private String aud;

  @Column(name = "exp")
  private Long exp;

  @Column(name = "iat")
  private Long iat;

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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getAud() {
    return aud;
  }

  public void setAud(String aud) {
    this.aud = aud;
  }

  public Long getExp() {
    return exp;
  }

  public void setExp(Long exp) {
    this.exp = exp;
  }

  public Long getIat() {
    return iat;
  }

  public void setIat(Long iat) {
    this.iat = iat;
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
