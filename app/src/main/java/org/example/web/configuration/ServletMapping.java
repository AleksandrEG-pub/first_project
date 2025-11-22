package org.example.web.configuration;

import jakarta.servlet.http.HttpServlet;

import java.util.Map;

public interface ServletMapping {
    Map<String, HttpServlet> getServletMapping();
}
