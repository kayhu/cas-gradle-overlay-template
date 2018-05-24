package org.iakuh.kauth.cas.dao.impl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.iakuh.kauth.cas.dao.TokenDao;
import org.iakuh.kauth.cas.dao.entity.Token;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Repository
@EnableTransactionManagement(proxyTargetClass = true)
@Transactional(transactionManager = "daoTransactionManager")
public class JpaTokenDaoImpl implements TokenDao {

  @PersistenceContext(unitName = "daoEntityManagerFactory")
  private EntityManager entityManager;

  @Override
  public Token findByToken(String token) {
    TypedQuery<Token> q = entityManager
        .createQuery("select t from Token t where t.token = :token", Token.class);
    q.setParameter("token", token);
    List<Token> resultList = q.getResultList();
    return resultList.isEmpty() ? null : resultList.get(0);
  }
}
