package org.iakuh.kauth.cas.controller;

import javax.servlet.http.HttpServletRequest;
import org.iakuh.kauth.cas.dao.entity.Token;
import org.iakuh.kauth.cas.service.UserService;
import org.iakuh.kauth.cas.token.TokenConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserDetailsController {

  @Autowired
  private UserService userService;

  @RequestMapping(value = "/userDetails", method = RequestMethod.GET)
  public UserDetails getUserDetails(@RequestParam String username, @RequestParam String tenant,
      @RequestParam String domain, HttpServletRequest request) {
    Token token = (Token) request.getAttribute(TokenConstants.ACCESS_TOKEN);

    if (!token.getDomain().getTenant().getCode().equals(tenant) ||
        !token.getDomain().getCode().equals(domain)) {
      throw new TokenAccessDeniedException();
    }

    UserDetails userDetails = userService.getUserDetails(username, tenant, domain);

    if (userDetails == null) {
      throw new UserDetailsNotFoundException();
    }

    return userDetails;
  }

  @ExceptionHandler(TokenAccessDeniedException.class)
  public ResponseEntity<String> onTokenAccessDeniedException(TokenAccessDeniedException e) {
    return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(UserDetailsNotFoundException.class)
  public ResponseEntity<String> onUserDetailsNotFoundException(UserDetailsNotFoundException e) {
    return new ResponseEntity<>("User details not found", HttpStatus.NOT_FOUND);
  }

  private static class TokenAccessDeniedException extends RuntimeException {

  }

  private static class UserDetailsNotFoundException extends RuntimeException {

  }

}
