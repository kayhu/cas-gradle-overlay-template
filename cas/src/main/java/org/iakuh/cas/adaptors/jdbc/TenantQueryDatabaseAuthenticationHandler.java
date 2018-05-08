package org.iakuh.cas.adaptors.jdbc;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.handler.PrincipalNameTransformer;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.iakuh.cas.authentication.TenantUsernamePasswordCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TenantQueryDatabaseAuthenticationHandler extends
    AbstractJdbcUsernamePasswordAuthenticationHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(TenantQueryDatabaseAuthenticationHandler.class);

  private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

  private PrincipalNameTransformer principalNameTransformer = formUserId -> formUserId;

  private final String sql;
  private final String fieldPassword;
  private final String fieldExpired;
  private final String fieldDisabled;
  private Map<String, String> principalAttributeMap = Collections.emptyMap();

  public TenantQueryDatabaseAuthenticationHandler(final String name,
      final ServicesManager servicesManager, final PrincipalFactory principalFactory,
      final Integer order, final DataSource dataSource, final String sql,
      final String fieldPassword, final String fieldExpired, final String fieldDisabled,
      final Map<String, String> attributes) {
    super(name, servicesManager, principalFactory, order, dataSource);
    this.sql = sql;
    this.fieldPassword = fieldPassword;
    this.fieldExpired = fieldExpired;
    this.fieldDisabled = fieldDisabled;
    this.principalAttributeMap = attributes;
  }

  @Override
  protected HandlerResult doAuthentication(Credential credential)
      throws GeneralSecurityException, PreventedException {
    final TenantUsernamePasswordCredential originalUserPass = (TenantUsernamePasswordCredential) credential;
    final TenantUsernamePasswordCredential userPass = new TenantUsernamePasswordCredential();
    userPass.setTenant(originalUserPass.getTenant());
    userPass.setUsername(originalUserPass.getUsername());
    userPass.setPassword(originalUserPass.getPassword());

    if (StringUtils.isBlank(userPass.getUsername())) {
      throw new AccountNotFoundException("Username is null.");
    }

    LOGGER.debug("Transforming credential username via [{}]",
        this.principalNameTransformer.getClass().getName());
    final String transformedUsername = this.principalNameTransformer
        .transform(userPass.getUsername());
    if (StringUtils.isBlank(transformedUsername)) {
      throw new AccountNotFoundException("Transformed username is null.");
    }

    if (StringUtils.isBlank(userPass.getPassword())) {
      throw new FailedLoginException("Password is null.");
    }

    LOGGER.debug("Attempting to encode credential password via [{}] for [{}]",
        this.passwordEncoder.getClass().getName(), transformedUsername);
    final String transformedPsw = this.passwordEncoder.encode(userPass.getPassword());
    if (StringUtils.isBlank(transformedPsw)) {
      throw new AccountNotFoundException("Encoded password is null.");
    }

    userPass.setUsername(transformedUsername);
    userPass.setPassword(transformedPsw);

    LOGGER.debug("Attempting authentication internally for transformed credential [{}]", userPass);
    return authenticateUsernamePasswordInternal(userPass, originalUserPass.getPassword());
  }

  @Override
  protected HandlerResult authenticateUsernamePasswordInternal(
      final UsernamePasswordCredential credential, final String originalPassword)
      throws GeneralSecurityException, PreventedException {

    if (StringUtils.isBlank(this.sql) || getJdbcTemplate() == null) {
      throw new GeneralSecurityException("Authentication handler is not configured correctly. "
          + "No SQL statement or JDBC template is found.");
    }

    final Map<String, Object> attributes = new LinkedHashMap<>(this.principalAttributeMap.size());
    final String tenant = ((TenantUsernamePasswordCredential) credential).getTenant();
    final String username = credential.getUsername();
    final String password = credential.getPassword();
    try {
      final Map<String, Object> dbFields = getJdbcTemplate()
          .queryForMap(this.sql, username, tenant);
      final String dbPassword = (String) dbFields.get(this.fieldPassword);

      if (StringUtils.isNotBlank(originalPassword) && !matches(originalPassword, dbPassword)
          || StringUtils.isBlank(originalPassword) && !StringUtils.equals(password, dbPassword)) {
        throw new FailedLoginException("Password does not match value on record.");
      }
      if (StringUtils.isNotBlank(this.fieldDisabled)) {
        final Object dbDisabled = dbFields.get(this.fieldDisabled);
        if (dbDisabled != null &&
            (Boolean.TRUE.equals(BooleanUtils.toBoolean(dbDisabled.toString()))
                || dbDisabled.equals(1))) {
          throw new AccountDisabledException("Account has been disabled");
        }
      }
      if (StringUtils.isNotBlank(this.fieldExpired)) {
        final Object dbExpired = dbFields.get(this.fieldExpired);
        if (dbExpired != null &&
            (Boolean.TRUE.equals(BooleanUtils.toBoolean(dbExpired.toString()))
                || dbExpired.equals(1))) {
          throw new AccountPasswordMustChangeException("Password has expired");
        }
      }

      this.principalAttributeMap.entrySet().forEach(a -> {
        final Object attribute = dbFields.get(a.getKey());
        if (attribute != null) {
          LOGGER.debug("Found attribute [{}] from the query results", a);
          final String principalAttrName = a.getValue();
          attributes.put(principalAttrName, attribute.toString());
        } else {
          LOGGER
              .warn("Requested attribute [{}] could not be found in the query results", a.getKey());
        }

      });

    } catch (final IncorrectResultSizeDataAccessException e) {
      if (e.getActualSize() == 0) {
        throw new AccountNotFoundException(
            "No record found for tenant[" + tenant + "] username[" + username + "]");
      }
      throw new FailedLoginException(
          "Multiple records found for tenant[" + tenant + "] username[" + username + "]");
    } catch (final DataAccessException e) {
      throw new PreventedException(
          "SQL exception while executing query for tenant[" + tenant + "] username[" + username
              + "]", e);
    }
    return createHandlerResult(credential,
        this.principalFactory.createPrincipal(username, attributes), null);
  }

  @Override
  public boolean supports(final Credential credential) {
    return credential instanceof TenantUsernamePasswordCredential && super.supports(credential);
  }
}
