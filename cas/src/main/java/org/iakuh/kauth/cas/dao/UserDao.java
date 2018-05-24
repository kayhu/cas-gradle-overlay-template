package org.iakuh.kauth.cas.dao;

import org.iakuh.kauth.cas.dao.entity.User;

public interface UserDao {

  User findById(Long id);

  User findByUsernameAndTenant(String username, String tenant);
}
