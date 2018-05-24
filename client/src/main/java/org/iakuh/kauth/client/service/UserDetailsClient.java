package org.iakuh.kauth.client.service;

import feign.Feign;
import feign.Logger.Level;
import feign.Param;
import feign.RequestInterceptor;
import feign.RequestLine;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import java.io.IOException;
import org.jasig.cas.client.util.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class UserDetailsClient implements InitializingBean {

  private String url;
  private String tenant;
  private String domain;
  private String token;
  private UserDetailsApi api;

  public UserDetailsClient() {
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTenant() {
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(this.url, "url must be set");
    Assert.notNull(this.tenant, "tenant must be set");
    Assert.notNull(this.domain, "domain must be set");
    Assert.notNull(this.token, "token must be set");
    this.api = initApi();
  }

  public UserDetails getUserDetails(String username) {
    return this.api.getUserDetails(username, this.tenant, this.domain);
  }

  private UserDetailsApi initApi() {
    return Feign.builder().logLevel(Level.FULL)
        .logger(new Slf4jLogger())
        .encoder(new JacksonEncoder())
        .decoder(new JacksonDecoder())
        .errorDecoder(new ResponseErrorDecoder())
        .requestInterceptor(new TokenRequestInterceptor(this.token))
        .target(UserDetailsApi.class, this.url);
  }

  private static class TokenRequestInterceptor implements RequestInterceptor {

    private String token;

    TokenRequestInterceptor(String token) {
      this.token = token;
    }

    @Override
    public void apply(RequestTemplate template) {
      template.header(HttpHeaders.AUTHORIZATION, "Bearer " + this.token);
    }
  }

  private static class ResponseErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
      try {
        final int status = response.status();
        final String message = IOUtils.readString(response.body().asInputStream(), IOUtils.UTF8);
        switch (status) {
          case 404:
            return new UsernameNotFoundException(message);
          default:
            return new UserDetailsException(status, message);
        }
      } catch (IOException e) {
        throw new RuntimeException("Failed to process response body.", e);
      }
    }
  }

  private interface UserDetailsApi {

    @RequestLine("GET /userDetails?username={username}&tenant={tenant}&domain={domain}")
    UserDetailsInfo getUserDetails(@Param("username") String username,
        @Param("tenant") String tenant, @Param("domain") String domain);
  }
}
