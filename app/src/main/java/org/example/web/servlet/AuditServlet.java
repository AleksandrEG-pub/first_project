package org.example.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.example.exception.ApplicationException;
import org.example.mapper.AuditLogMapper;
import org.example.service.AuditService;

/** Servlet for handling audit log retrieval requests. */
public class AuditServlet extends HttpServlet {

  private static final String USERNAME_PARAMETER = "username";
  private static final AuditLogMapper AUDIT_LOG_MAPPER = AuditLogMapper.INSTANCE;
  private final transient AuditService auditService;
  private final ObjectMapper objectMapper;

  public AuditServlet(AuditService auditService, ObjectMapper objectMapper) {
    this.auditService = auditService;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    if (hasSearchParameters(req)) {
      handleSearch(req, resp);
    } else {
      handleGetAll(resp);
    }
  }

  /** Checks if request has username search parameter. */
  private boolean hasSearchParameters(HttpServletRequest req) {
    return req.getParameter(USERNAME_PARAMETER) != null;
  }

  /** Handles search by username. */
  private void handleSearch(HttpServletRequest req, HttpServletResponse resp) {
    String username = req.getParameter(USERNAME_PARAMETER);
    var auditLogs =
        auditService.findByUsername(username).stream().map(AUDIT_LOG_MAPPER::toDto).toList();
    writeResponseJson(resp, auditLogs);
  }

  /** Handles retrieval of all audit logs. */
  private void handleGetAll(HttpServletResponse resp) {
    var auditLogs = auditService.findAll().stream().map(AUDIT_LOG_MAPPER::toDto).toList();
    writeResponseJson(resp, auditLogs);
  }

  /** Writes JSON response with the provided object. */
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
