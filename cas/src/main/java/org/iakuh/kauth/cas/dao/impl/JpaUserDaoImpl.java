package org.iakuh.kauth.cas.dao.impl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.iakuh.kauth.cas.dao.UserDao;
import org.iakuh.kauth.cas.dao.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Repository
@EnableTransactionManagement(proxyTargetClass = true)
@Transactional(transactionManager = "daoTransactionManager")
public class JpaUserDaoImpl implements UserDao {

  @PersistenceContext(unitName = "daoEntityManagerFactory")
  private EntityManager entityManager;

  @Override
  public User findById(Long id) {
    return entityManager.find(User.class, id);
  }

  @Override
  public User findByUsernameAndTenant(String username, String tenant) {
    TypedQuery<User> q = entityManager
        .createQuery("select u from User u"
            + " join Tenant t on u.tenantId = t.id"
            + " where u.username = :username"
            + " and t.code = :tenant", User.class);
    q.setParameter("username", username);
    q.setParameter("tenant", tenant);
    List<User> resultList = q.getResultList();
    return resultList.isEmpty() ? null : resultList.get(0);
  }
}
