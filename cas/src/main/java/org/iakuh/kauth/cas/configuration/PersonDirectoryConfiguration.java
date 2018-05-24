//package org.iakuh.kauth.cas.configuration;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import org.apache.commons.lang3.StringUtils;
//import org.apereo.cas.authentication.principal.PrincipalFactory;
//import org.apereo.cas.authentication.principal.PrincipalResolver;
//import org.apereo.cas.authentication.principal.resolvers.ChainingPrincipalResolver;
//import org.apereo.cas.authentication.principal.resolvers.EchoingPrincipalResolver;
//import org.apereo.cas.configuration.CasConfigurationProperties;
//import org.apereo.cas.configuration.model.core.authentication.PrincipalAttributesProperties;
//import org.apereo.cas.configuration.support.Beans;
//import org.apereo.cas.util.CollectionUtils;
//import org.apereo.services.persondir.IPersonAttributeDao;
//import org.apereo.services.persondir.support.CachingPersonAttributeDaoImpl;
//import org.apereo.services.persondir.support.MergingPersonAttributeDaoImpl;
//import org.apereo.services.persondir.support.SimpleUsernameAttributeProvider;
//import org.apereo.services.persondir.support.jdbc.AbstractJdbcPersonAttributeDao;
//import org.apereo.services.persondir.support.jdbc.MultiRowJdbcPersonAttributeDao;
//import org.apereo.services.persondir.support.jdbc.SingleRowJdbcPersonAttributeDao;
//import org.apereo.services.persondir.support.merger.MultivaluedAttributeMerger;
//import org.apereo.services.persondir.support.merger.NoncollidingAttributeAdder;
//import org.apereo.services.persondir.support.merger.ReplacingAttributeAdder;
//import org.iakuh.kauth.cas.authentication.TenantPersonDirectoryPrincipalResolver;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.OrderComparator;
//
//@Configuration
//@EnableConfigurationProperties(CasConfigurationProperties.class)
//public class PersonDirectoryConfiguration {
//
//  private static final Logger LOGGER = LoggerFactory.getLogger(PersonDirectoryConfiguration.class);
//
//  @Autowired
//  private CasConfigurationProperties casProperties;
//
//
//  @Autowired
//  @RefreshScope
//  @Bean("personDirectoryPrincipalResolver")
//  public PrincipalResolver personDirectoryPrincipalResolver(
//      @Qualifier("principalFactory") final PrincipalFactory principalFactory) {
//    final TenantPersonDirectoryPrincipalResolver bean = new TenantPersonDirectoryPrincipalResolver();
//    bean.setAttributeRepository(attributeRepository());
//    bean.setPrincipalAttributeName(casProperties.getPersonDirectory().getPrincipalAttribute());
//    bean.setReturnNullIfNoAttributes(casProperties.getPersonDirectory().isReturnNull());
//    bean.setPrincipalFactory(principalFactory);
//
//    final ChainingPrincipalResolver resolver = new ChainingPrincipalResolver();
//    if (!attributeRepositories().isEmpty()) {
//      LOGGER.debug(
//          "Attribute repository sources are defined and available for the principal resolution chain. "
//              + "The principal resolver will use a combination of attributes collected from attribute repository sources "
//              + "and whatever may be collected during the authentication phase where results are eventually merged.");
//      resolver.setChain(CollectionUtils.wrapList(bean, new EchoingPrincipalResolver()));
//    } else {
//      LOGGER.debug(
//          "Attribute repository sources are not available for principal resolution so principal resolver will echo "
//              + "back the principal resolved during authentication directly.");
//      resolver.setChain(new EchoingPrincipalResolver());
//    }
//
//    return resolver;
//  }
//
//  @RefreshScope
//  @Bean("attributeRepositories")
//  public List<IPersonAttributeDao> attributeRepositories() {
//    final List<IPersonAttributeDao> list = new ArrayList<>();
//
//    list.addAll(jdbcAttributeRepositories());
//
//    OrderComparator.sort(list);
//
//    LOGGER.debug("Final list of attribute repositories is [{}]", list);
//    return list;
//  }
//
//  @RefreshScope
//  @Bean("attributeRepository")
//  public IPersonAttributeDao attributeRepository() {
//    return composeMergedAndCachedAttributeRepositories(attributeRepositories());
//  }
//
//  @RefreshScope
//  @Bean
//  public List<IPersonAttributeDao> jdbcAttributeRepositories() {
//    final List<IPersonAttributeDao> list = new ArrayList<>();
//    final PrincipalAttributesProperties attrs = casProperties.getAuthn().getAttributeRepository();
//    attrs.getJdbc().forEach(jdbc -> {
//      if (StringUtils.isNotBlank(jdbc.getSql()) && StringUtils.isNotBlank(jdbc.getUrl())) {
//        final AbstractJdbcPersonAttributeDao jdbcDao;
//
//        if (jdbc.isSingleRow()) {
//          LOGGER.debug("Configured single-row JDBC attribute repository for [{}]", jdbc.getUrl());
//          jdbcDao = new SingleRowJdbcPersonAttributeDao(
//              Beans.newDataSource(jdbc),
//              jdbc.getSql()
//          );
//        } else {
//          LOGGER.debug("Configured multi-row JDBC attribute repository for [{}]", jdbc.getUrl());
//          jdbcDao = new MultiRowJdbcPersonAttributeDao(
//              Beans.newDataSource(jdbc),
//              jdbc.getSql()
//          );
//          LOGGER.debug("Configured multi-row JDBC column mappings for [{}] are [{}]", jdbc.getUrl(),
//              jdbc.getColumnMappings());
//          ((MultiRowJdbcPersonAttributeDao) jdbcDao)
//              .setNameValueColumnMappings(jdbc.getColumnMappings());
//        }
//
//        jdbcDao.setUsernameAttributeProvider(usernameAttributeProvider());
//        jdbcDao.setQueryAttributeMapping(
//            Collections.singletonMap(FIELD_PRINCIPAL_ID, jdbc.getUsername()));
//        final Map<String, String> mapping = jdbc.getAttributes();
//        if (mapping != null && !mapping.isEmpty()) {
//          LOGGER.debug("Configured result attribute mapping for [{}] to be [{}]", jdbc.getUrl(),
//              jdbc.getAttributes());
//          jdbcDao.setResultAttributeMapping(mapping);
//        }
//        jdbcDao.setRequireAllQueryAttributes(jdbc.isRequireAllAttributes());
//        jdbcDao.setUsernameCaseCanonicalizationMode(jdbc.getCaseCanonicalization());
//        jdbcDao.setDefaultCaseCanonicalizationMode(jdbc.getCaseCanonicalization());
//        jdbcDao.setQueryType(jdbc.getQueryType());
//        jdbcDao.setOrder(jdbc.getOrder());
//        list.add(jdbcDao);
//      }
//    });
//    return list;
//  }
//
//  @Bean
//  public SimpleUsernameAttributeProvider usernameAttributeProvider() {
//    return new SimpleUsernameAttributeProvider(FIELD_PRINCIPAL_ID);
//  }
//
//  private IPersonAttributeDao composeMergedAndCachedAttributeRepositories(
//      final List<IPersonAttributeDao> list) {
//    final MergingPersonAttributeDaoImpl mergingDao = new MergingPersonAttributeDaoImpl();
//
//    final String merger = StringUtils
//        .defaultIfBlank(casProperties.getAuthn().getAttributeRepository().getMerger(),
//            "replace".trim());
//    LOGGER.debug("Configured merging strategy for attribute sources is [{}]", merger);
//    switch (merger.toLowerCase()) {
//      case "merge":
//        mergingDao.setMerger(new MultivaluedAttributeMerger());
//        break;
//      case "add":
//        mergingDao.setMerger(new NoncollidingAttributeAdder());
//        break;
//      case "replace":
//      default:
//        mergingDao.setMerger(new ReplacingAttributeAdder());
//        break;
//    }
//
//    final CachingPersonAttributeDaoImpl impl = new CachingPersonAttributeDaoImpl();
//    impl.setCacheNullResults(false);
//
//    final Cache graphs = CacheBuilder.newBuilder()
//        .concurrencyLevel(2)
//        .weakKeys()
//        .maximumSize(casProperties.getAuthn().getAttributeRepository().getMaximumCacheSize())
//        .expireAfterWrite(casProperties.getAuthn().getAttributeRepository().getExpireInMinutes(),
//            TimeUnit.MINUTES)
//        .build();
//    impl.setUserInfoCache(graphs.asMap());
//    mergingDao.setPersonAttributeDaos(list);
//    impl.setCachedPersonAttributesDao(mergingDao);
//    impl.setUsernameAttributeProvider(usernameAttributeProvider());
//
//    if (list.isEmpty()) {
//      LOGGER.debug("No attribute repository sources are available/defined to merge together.");
//    } else {
//      LOGGER.debug("Configured attribute repository sources to merge together: [{}]", list);
//      LOGGER.debug(
//          "Configured cache expiration policy for merging attribute sources to be [{}] minute(s)",
//          casProperties.getAuthn().getAttributeRepository().getExpireInMinutes());
//    }
//    return impl;
//  }
//}
