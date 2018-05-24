package org.iakuh.kauth.client.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsService implements
    org.springframework.security.core.userdetails.UserDetailsService {

  private UserDetailsClient userDetailsClient;

  public UserDetailsService() {
  }

  public UserDetailsClient getUserDetailsClient() {
    return userDetailsClient;
  }

  public void setUserDetailsClient(UserDetailsClient userDetailsClient) {
    this.userDetailsClient = userDetailsClient;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userDetailsClient.getUserDetails(username);
  }
}
