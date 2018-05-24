package org.iakuh.kauth.cas.dao;

import org.iakuh.kauth.cas.dao.entity.Token;

public interface TokenDao {

  Token findByToken(String token);
}
