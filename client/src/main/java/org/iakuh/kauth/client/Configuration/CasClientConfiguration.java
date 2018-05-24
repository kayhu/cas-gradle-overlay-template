package org.iakuh.kauth.client.Configuration;

import org.iakuh.kauth.client.service.UserDetailsClient;
import org.iakuh.kauth.client.service.UserDetailsService;
import org.iakuh.kauth.client.service.UserDetailsServiceWrapper;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class CasClientConfiguration extends WebSecurityConfigurerAdapter {

  @Bean
  public ServiceProperties serviceProperties() {
    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setService("https://localhost:9443/cas-sample/login/cas");
    serviceProperties.setSendRenew(false);
    serviceProperties.setAuthenticateAllArtifacts(true);
    return serviceProperties;
  }

  @Bean
  public UserDetailsClient userDetailsClient() {
    UserDetailsClient userDetailsClient = new UserDetailsClient();
    userDetailsClient.setUrl("https://localhost:8443/cas");
    userDetailsClient.setTenant("iakuh");
    userDetailsClient.setDomain("skeleton");
    userDetailsClient.setToken("ZDg0OTA2YzA0ZmI0NGY5MjkyNDNhNTY0NGYwMDViYWY");
    return userDetailsClient;
  }

  @Bean
  public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> AuthenticationUserDetailsService() {
    UserDetailsService userDetailsService = new UserDetailsService();
    userDetailsService.setUserDetailsClient(userDetailsClient());
    return new UserDetailsServiceWrapper<>(userDetailsService);
  }

  @Bean
  public CasAuthenticationProvider casAuthenticationProvider() {
    CasAuthenticationProvider p = new CasAuthenticationProvider();
    p.setAuthenticationUserDetailsService(AuthenticationUserDetailsService());
    p.setServiceProperties(serviceProperties());
    p.setTicketValidator(new Cas30ServiceTicketValidator("https://localhost:8443/cas"));
    p.setKey("cas-auth-provider");
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
    LogoutFilter logoutFilter = new LogoutFilter("https://localhost:8443/cas/logout",
        new SecurityContextLogoutHandler());
    logoutFilter.setFilterProcessesUrl("/logout/cas");
    return logoutFilter;
  }

  @Bean
  public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
    CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
    casAuthenticationEntryPoint.setLoginUrl("https://localhost:8443/cas/login");
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
