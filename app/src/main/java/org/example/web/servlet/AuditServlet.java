package org.example.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.example.exception.ApplicationException;
import org.example.model.AuditLog;
import org.example.service.AuditService;

public class AuditServlet extends HttpServlet {

  private static final String USERNAME_PARAMETER = "username";
  private final transient AuditService auditService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public AuditServlet(AuditService auditService) {
    this.auditService = auditService;
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    if (hasSearchParameters(req)) {
      handleSearch(req, resp);
    } else {
      handleGetAll(resp);
    }
  }

  private boolean hasSearchParameters(HttpServletRequest req) {
    return req.getParameter(USERNAME_PARAMETER) != null;
  }

  private void handleSearch(HttpServletRequest req, HttpServletResponse resp) {
    String username = req.getParameter(USERNAME_PARAMETER);
    List<AuditLog> auditLogs = auditService.findByUsername(username);
    writeResponseJson(resp, auditLogs);
  }

  private void handleGetAll(HttpServletResponse resp) {
    List<AuditLog> auditLogs = auditService.findAll();
    writeResponseJson(resp, auditLogs);
  }

  private void writeResponseJson(HttpServletResponse resp, Object object) {
    resp.setContentType("application/json");
    resp.setStatus(HttpServletResponse.SC_OK);
    try (PrintWriter writer = resp.getWriter()) {
      String json = objectMapper.writer().writeValueAsString(object);
      writer.write(json);
    } catch (IOException e) {
      throw new ApplicationException("Failed to write response", e);
    }
  }
}
