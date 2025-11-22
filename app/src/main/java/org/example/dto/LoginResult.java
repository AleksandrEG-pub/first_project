package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {
  boolean isSuccess = false;
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
