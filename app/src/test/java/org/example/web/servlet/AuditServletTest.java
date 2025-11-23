package org.example.web.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.example.exception.ApplicationException;
import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.example.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class AuditServletTest {

  @Mock private AuditService auditService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  private ObjectMapper objectMapper;
  private AuditServlet auditServlet;
  private StringWriter responseWriter;

  @BeforeEach
  void setUp() throws IOException {
    auditService = Mockito.mock(AuditService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    auditServlet = new AuditServlet(auditService, objectMapper);
    responseWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  void doGet_WhenNoUsernameParameter_ShouldReturnAllAuditLogs() throws IOException {
    // Arrange
    List<AuditLog> auditLogs =
        List.of(
            createAuditLog(1L, "user1", AuditAction.SEARCH),
            createAuditLog(2L, "user2", AuditAction.SEARCH));
    when(auditService.findAll()).thenReturn(auditLogs);
    when(request.getParameter("username")).thenReturn(null);

    // Act
    auditServlet.doGet(request, response);

    // Assert
    verify(auditService).findAll();
    verify(auditService, never()).findByUsername(anyString());

    String jsonResponse = responseWriter.toString();
    List<?> responseList = objectMapper.readValue(jsonResponse, List.class);

    assertThat(responseList).hasSize(2);
    verify(response).setContentType("application/json");
    verify(response).setStatus(HttpServletResponse.SC_OK);
  }

  private AuditLog createAuditLog(Long id, String username, AuditAction action) {
    AuditLog auditLog = new AuditLog();
    auditLog.setId(id);
    auditLog.setUsername(username);
    auditLog.setAction(action);
    auditLog.setTimestamp(java.time.LocalDateTime.now());
    return auditLog;
  }

  @Test
  void doGet_WhenUsernameParameterProvided_ShouldReturnFilteredAuditLogs() throws IOException {
    // Arrange
    String username = "testUser";
    List<AuditLog> auditLogs =
        List.of(
            createAuditLog(1L, username, AuditAction.LOGIN),
            createAuditLog(2L, username, AuditAction.LOGOUT));
    when(auditService.findByUsername(username)).thenReturn(auditLogs);
    when(request.getParameter("username")).thenReturn(username);

    // Act
    auditServlet.doGet(request, response);

    // Assert
    verify(auditService).findByUsername(username);
    verify(auditService, never()).findAll();

    String jsonResponse = responseWriter.toString();
    List<?> responseList = objectMapper.readValue(jsonResponse, List.class);

    assertThat(responseList).hasSize(2);
    verify(response).setContentType("application/json");
    verify(response).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  void doGet_WhenUsernameParameterEmpty_ShouldReturnFilteredAuditLogs() throws JsonProcessingException {
    // Arrange
    String username = "";
    List<AuditLog> auditLogs = List.of(createAuditLog(1L, "", AuditAction.SEARCH));
    when(auditService.findByUsername(username)).thenReturn(auditLogs);
    when(request.getParameter("username")).thenReturn(username);

    // Act
    auditServlet.doGet(request, response);

    // Assert
    String jsonResponse = responseWriter.toString();
    List<?> responseList = objectMapper.readValue(jsonResponse, List.class);

    assertThat(responseList).hasSize(1);
    verify(auditService).findByUsername(username);
    verify(response).setContentType("application/json");
    verify(response).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  void doGet_WhenNoAuditLogsFound_ShouldReturnEmptyArray() {
    // Arrange
    when(auditService.findAll()).thenReturn(List.of());
    when(request.getParameter("username")).thenReturn(null);

    // Act
    auditServlet.doGet(request, response);

    // Assert
    String jsonResponse = responseWriter.toString();
    assertThat(jsonResponse).isEqualTo("[]");
    verify(response).setContentType("application/json");
    verify(response).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  void doGet_WhenIOExceptionOccurs_ShouldThrowApplicationException() throws IOException {
    // Arrange
    when(auditService.findAll())
        .thenReturn(List.of(createAuditLog(1L, "user", AuditAction.SEARCH)));
    when(request.getParameter("username")).thenReturn(null);

    // Simulate IOException when getting writer
    when(response.getWriter()).thenThrow(new IOException("Test IO Exception"));

    // Act & Assert
    assertThatThrownBy(() -> auditServlet.doGet(request, response))
        .isInstanceOf(ApplicationException.class)
        .hasMessage("Failed to write response")
        .hasCauseInstanceOf(IOException.class);
  }
}
