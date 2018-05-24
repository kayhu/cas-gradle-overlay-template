package org.iakuh.kauth.client.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class UserDetailsServiceWrapper<T extends Authentication> implements
    AuthenticationUserDetailsService<T>, InitializingBean {

  private UserDetailsService userDetailsService = null;

  public UserDetailsServiceWrapper() {
    // constructor for backwards compatibility with 2.0
  }

  public UserDetailsServiceWrapper(final UserDetailsService userDetailsService) {
    Assert.notNull(userDetailsService, "userDetailsService cannot be null.");
    this.userDetailsService = userDetailsService;
  }

  public void afterPropertiesSet() throws Exception {
    Assert.notNull(this.userDetailsService, "UserDetailsService must be set");
  }

  public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
    return this.userDetailsService.loadUserByUsername(authentication.getName());
  }

  public void setUserDetailsService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }
}