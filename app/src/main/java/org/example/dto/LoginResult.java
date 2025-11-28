package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {
  /** if login attempt was successful */
  boolean isSuccess = false;

  /**
   * additional information about login attempt.
   *
   * <p>In case of error can contain user friendly information about fail
   */
  String message = "";

  public LoginResult(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public LoginResult(String message) {
    this.message = message;
  }

  public boolean isFailure() {
    return !isSuccess;
  }
}
