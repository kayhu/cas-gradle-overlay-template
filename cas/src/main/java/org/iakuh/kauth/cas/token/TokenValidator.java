package org.iakuh.kauth.cas.token;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.iakuh.kauth.cas.dao.TokenDao;
import org.iakuh.kauth.cas.dao.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class TokenValidator {

  @Autowired
  private TokenDao tokenDao;

  public boolean validate(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String tokenString = TokenExtractor.extractToken(request);

    if (StringUtils.isBlank(tokenString)) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write("Token is empty");
      return false;
    }

    Token token = tokenDao.findByToken(tokenString);
    if (token == null) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write("Token is invalid");
      return false;
    }

    if (System.currentTimeMillis() >= token.getExp()) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write("Token is expired");
      return false;
    }

    request.setAttribute(TokenConstants.ACCESS_TOKEN, token);
    return true;
  }

  private static class TokenExtractor {

    public static String extractToken(HttpServletRequest request) {
      Enumeration<String> headers = request.getHeaders(HttpHeaders.AUTHORIZATION);
      while (headers.hasMoreElements()) {
        String header = headers.nextElement();
        if (header.startsWith(TokenConstants.BEARER_TYPE)) {
          return header.substring(TokenConstants.BEARER_TYPE.length()).trim();
        }
      }
      return null;
    }
  }
}

