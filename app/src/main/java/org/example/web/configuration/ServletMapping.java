package org.example.web.configuration;

import jakarta.servlet.http.HttpServlet;
import java.util.Map;

/** Provides mappings between URL patterns and servlets. */
public interface ServletMapping {

  /** Returns the servlet mappings (URL pattern -> servlet). */
  Map<String, HttpServlet> getServletMapping();
}
