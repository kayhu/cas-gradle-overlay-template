package org.iakuh.kauth.client.Configuration;

import org.iakuh.kauth.client.service.UserDetailsClient;
import org.iakuh.kauth.client.service.UserDetailsService;
import org.iakuh.kauth.client.service.UserDetailsServiceWrapper;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class CasClientConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private Environment env;

  @Bean
  public ServiceProperties serviceProperties() {
    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setService(env.getProperty("cas.client.service"));
    serviceProperties.setSendRenew(false);
    serviceProperties.setAuthenticateAllArtifacts(true);
    return serviceProperties;
  }

  @Bean
  public UserDetailsClient userDetailsClient() {
    UserDetailsClient userDetailsClient = new UserDetailsClient();
    userDetailsClient.setUrl(env.getProperty("cas.server.url.prefix"));
    userDetailsClient.setTenant(env.getProperty("cas.client.tenant"));
    userDetailsClient.setDomain(env.getProperty("cas.client.domain"));
    userDetailsClient.setToken(env.getProperty("cas.client.token"));
    return userDetailsClient;
  }

  @Bean
  public UserDetailsServiceWrapper<CasAssertionAuthenticationToken> userDetailsServiceWrapper() {
    UserDetailsService userDetailsService = new UserDetailsService();
    userDetailsService.setUserDetailsClient(userDetailsClient());
    return new UserDetailsServiceWrapper<>(userDetailsService);
  }

  @Bean
  public CasAuthenticationProvider casAuthenticationProvider() {
    CasAuthenticationProvider p = new CasAuthenticationProvider();
    p.setAuthenticationUserDetailsService(userDetailsServiceWrapper());
    p.setServiceProperties(serviceProperties());
    p.setTicketValidator(new Cas30ServiceTicketValidator(env.getProperty("cas.server.url.prefix")));
    p.setKey(env.getProperty("cas.client.key"));
    return p;
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
    CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
    casAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
    return casAuthenticationFilter;
  }

  @Bean
  public SingleSignOutFilter singleSignOutFilter() {
    return new SingleSignOutFilter();
  }

  @Bean
  public LogoutFilter logoutFilter() {
    LogoutFilter logoutFilter = new LogoutFilter(env.getProperty("cas.server.logout.url"),
        new SecurityContextLogoutHandler());
    logoutFilter.setFilterProcessesUrl(env.getProperty("cas.client.logout.filter.url", "/logout"));
    return logoutFilter;
  }

  @Bean
  public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
    CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
    casAuthenticationEntryPoint.setLoginUrl(env.getProperty("cas.server.login.url"));
    casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
    return casAuthenticationEntryPoint;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint())
        .and().addFilterAt(casAuthenticationFilter(), CasAuthenticationFilter.class)
        .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
        .addFilterBefore(logoutFilter(), LogoutFilter.class)
        .authorizeRequests().antMatchers("/**").authenticated();
  }
}
