package org.example.web;

import java.util.regex.Pattern;

public class RequestPathTools {

  /** Pattern: /123 (get by ID) */
  private static final Pattern idPathParamPattern = Pattern.compile("/\\d+");

  private RequestPathTools() {
  }

  public static boolean isPathInfoHasId(String pathInfo) {
    return pathInfo != null && idPathParamPattern.matcher(pathInfo).matches();
  }
}
