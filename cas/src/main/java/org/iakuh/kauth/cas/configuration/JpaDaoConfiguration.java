package org.iakuh.kauth.cas.configuration;

import static org.apereo.cas.configuration.support.Beans.newDataSource;
import static org.apereo.cas.configuration.support.Beans.newHibernateEntityManagerFactoryBean;
import static org.apereo.cas.configuration.support.Beans.newHibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jpa.JpaConfigDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration("jpaDaoConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class JpaDaoConfiguration {

  @Autowired
  private CasConfigurationProperties casProperties;

  @Bean
  public String[] jpaDaoPackagesToScan() {
    return new String[]{"org.iakuh.kauth.cas.dao"};
  }

  @RefreshScope
  @Bean
  public HibernateJpaVendorAdapter jpaDaoVendorAdapter() {
    return newHibernateJpaVendorAdapter(casProperties.getJdbc());
  }

  @Lazy
  @Bean
  public LocalContainerEntityManagerFactoryBean daoEntityManagerFactory() {
    return newHibernateEntityManagerFactoryBean(
        new JpaConfigDataHolder(
            jpaDaoVendorAdapter(),
            "jpaDaoContext",
            jpaDaoPackagesToScan(),
            daoDataSource()),
        casProperties.getServiceRegistry().getJpa());
  }

  @Autowired
  @Bean
  public PlatformTransactionManager daoTransactionManager(
      @Qualifier("daoEntityManagerFactory") final EntityManagerFactory emf) {
    final JpaTransactionManager mgmr = new JpaTransactionManager();
    mgmr.setEntityManagerFactory(emf);
    return mgmr;
  }

  @RefreshScope
  @Primary
  public DataSource daoDataSource() {
    return newDataSource(casProperties.getServiceRegistry().getJpa());
  }
}
