package org.iakuh.kauth.cas.dao.entity;

import java.time.ZonedDateTime;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AbstractEntity {

  abstract Long getId();

  abstract void setId(Long id);

  abstract ZonedDateTime getCreateTime();

  abstract void setCreateTime(ZonedDateTime createTime);

  abstract ZonedDateTime getUpdateTime();

  abstract void setUpdateTime(ZonedDateTime updateTime);

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
