package org.iakuh.kauth.client.test;

import org.iakuh.kauth.client.service.UserDetailsClient;
import org.iakuh.kauth.client.service.UserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Configuration
@ContextConfiguration(classes = UserDetailsServiceTest.class)
public class UserDetailsServiceTest {

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
  public UserDetailsService userDetailsService() {
    UserDetailsService userDetailsService = new UserDetailsService();
    userDetailsService.setUserDetailsClient(userDetailsClient());
    return userDetailsService;
  }

  @Autowired
  private UserDetailsService userDetailsService;

  @Test
  public void test() {
    UserDetails user = userDetailsService.loadUserByUsername("huk3");
  }
}
