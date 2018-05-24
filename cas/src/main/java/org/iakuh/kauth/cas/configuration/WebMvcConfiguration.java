package org.iakuh.kauth.cas.configuration;

import org.iakuh.kauth.cas.token.TokenValidationInterceptor;
import org.iakuh.kauth.cas.token.TokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

  @Bean
  public TokenValidator tokenValidator() {
    return new TokenValidator();
  }

  @Bean
  public TokenValidationInterceptor tokenValidationInterceptor() {
    return new TokenValidationInterceptor(tokenValidator());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tokenValidationInterceptor()).addPathPatterns("/userDetails");
  }
}
