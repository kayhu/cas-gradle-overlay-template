package org.iakuh.kauth.cas.service;

import static org.springframework.security.core.userdetails.User.withUsername;

import java.util.List;
import java.util.stream.Collectors;
import org.iakuh.kauth.cas.dao.UserDao;
import org.iakuh.kauth.cas.dao.entity.Role;
import org.iakuh.kauth.cas.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserDao userDao;

  public UserDetails getUserDetails(String username, String tenant, String domain) {
    User user = userDao.findByUsernameAndTenant(username, tenant);

    if (user == null) {
      return null;
    }

    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
        .filter(role -> role.getDomain().getCode().equals(domain))
        .map(Role::getCode).map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    return withUsername(user.getUsername()).password("********")
        .authorities(authorities).disabled(user.getDisabled())
        .accountExpired(false).accountLocked(false)
        .credentialsExpired(false).build();
  }

}
