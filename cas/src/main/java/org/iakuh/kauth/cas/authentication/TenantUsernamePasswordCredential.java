package org.iakuh.kauth.cas.authentication;

import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apereo.cas.authentication.UsernamePasswordCredential;

public class TenantUsernamePasswordCredential extends UsernamePasswordCredential {

  private static final long serialVersionUID = 16550737109157401L;

  @Size(min = 1, message = "required.tenant")
  private String tenant;

  public String getTenant() {
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TenantUsernamePasswordCredential other = (TenantUsernamePasswordCredential) obj;
    return this.tenant.equals(other.tenant);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .appendSuper(super.hashCode())
        .append(this.tenant)
        .toHashCode();
  }
}
