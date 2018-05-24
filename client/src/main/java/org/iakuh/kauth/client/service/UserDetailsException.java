package org.iakuh.kauth.client.service;

public class UserDetailsException extends Exception {

  private int status;
  private String message;

  public UserDetailsException() {
    super();
  }

  public UserDetailsException(int status, String message) {
    super(message);
    this.status = status;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
