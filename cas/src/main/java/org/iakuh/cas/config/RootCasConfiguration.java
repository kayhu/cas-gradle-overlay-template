package org.iakuh.cas.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.JdbcAuthenticationProperties;
import org.apereo.cas.configuration.support.Beans;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.iakuh.cas.adaptors.jdbc.TenantQueryDatabaseAuthenticationHandler;
import org.iakuh.cas.web.flow.WebflowConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@Configuration
public class RootCasConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(RootCasConfiguration.class);

  @Autowired
  @Qualifier("servicesManager")
  private ServicesManager servicesManager;

  @Autowired
  @Qualifier("jdbcPrincipalFactory")
  private PrincipalFactory jdbcPrincipalFactory;

  @Autowired(required = false)
  @Qualifier("queryPasswordPolicyConfiguration")
  private PasswordPolicyConfiguration queryPasswordPolicyConfiguration;

  @Autowired
  private CasConfigurationProperties casProperties;

  @Bean("defaultWebflowConfigurer")
  public CasWebflowConfigurer defaultWebflowConfigurer(FlowBuilderServices builderServices,
      FlowDefinitionRegistry loginFlowRegistry, FlowDefinitionRegistry logoutFlowRegistry) {
    final WebflowConfigurer c = new WebflowConfigurer(builderServices, loginFlowRegistry);
    c.setLogoutFlowDefinitionRegistry(logoutFlowRegistry);
    return c;
  }

  @Bean("jdbcAuthenticationHandlers")
  @RefreshScope
  public Collection<AuthenticationHandler> jdbcAuthenticationHandlers() {
    final Collection<AuthenticationHandler> handlers = new HashSet<>();
    final JdbcAuthenticationProperties jdbc = casProperties.getAuthn().getJdbc();
    jdbc.getQuery().forEach(b -> handlers.add(tenantQueryDatabaseAuthenticationHandler(b)));
    return handlers;
  }

  private AuthenticationHandler tenantQueryDatabaseAuthenticationHandler(
      final JdbcAuthenticationProperties.Query b) {
    final Map<String, String> attributes = Beans
        .transformPrincipalAttributesListIntoMap(b.getPrincipalAttributeList());
    LOGGER
        .debug("Created and mapped principal attributes [{}] for [{}]...", attributes, b.getUrl());

    final TenantQueryDatabaseAuthenticationHandler h = new TenantQueryDatabaseAuthenticationHandler(
        b.getName(), servicesManager, jdbcPrincipalFactory,
        b.getOrder(), Beans.newDataSource(b), b.getSql(),
        b.getFieldPassword(), b.getFieldExpired(), b.getFieldDisabled(),
        attributes);

    h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
    h.setPrincipalNameTransformer(
        Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

    if (queryPasswordPolicyConfiguration != null) {
      h.setPasswordPolicyConfiguration(queryPasswordPolicyConfiguration);
    }

    h.setPrincipalNameTransformer(
        Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

    if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
      h.setCredentialSelectionPredicate(
          Beans.newCredentialSelectionPredicate(b.getCredentialCriteria()));
    }

    LOGGER.debug("Created authentication handler [{}] to handle database url at [{}]", h.getName(),
        b.getUrl());
    return h;
  }

}
