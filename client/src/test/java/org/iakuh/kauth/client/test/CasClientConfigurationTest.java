package org.iakuh.kauth.client.test;

import static junit.framework.TestCase.assertNotNull;

import org.iakuh.kauth.client.Configuration.CasClientConfiguration;
import org.iakuh.kauth.client.service.UserDetailsClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Configuration
@ContextConfiguration(classes = {CasClientConfiguration.class, CasClientConfigurationTest.class})
@PropertySource("classpath:config.properties")
public class CasClientConfigurationTest {

  @Autowired
  private UserDetailsClient userDetailsClient;

  @Test
  public void test() {
    UserDetails user = userDetailsClient.getUserDetails("huk");
    assertNotNull(user);
  }
}
