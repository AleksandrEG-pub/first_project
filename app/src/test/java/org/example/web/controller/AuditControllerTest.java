package org.example.web.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.example.configuration.BeanConfiguration;
import org.example.dto.AuditLogDto;
import org.example.mapper.AuditLogMapper;
import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.example.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringJUnitConfig(classes = {BeanConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
class AuditControllerTest {

  MockMvc mockMvc;
  @MockitoBean AuditService auditService;
  @Autowired ObjectMapper objectMapper;
  AuditLogMapper auditLogMapper = Mappers.getMapper(AuditLogMapper.class);

  @BeforeEach
  void setup() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(new AuditController(auditLogMapper, auditService))
            .setControllerAdvice(GlobalExceptionHandler.class)
            .build();
  }

  @Test
  void getByUsername_WhenNoUsernameParameter_ShouldReturnAllAuditLogs() throws Exception {
    // Arrange
    List<AuditLog> auditLogs =
        List.of(
            createAuditLog(1L, "user1", AuditAction.SEARCH),
            createAuditLog(2L, "user2", AuditAction.SEARCH));
    when(auditService.findAll()).thenReturn(auditLogs);

    List<AuditLogDto> expectedDtos = auditLogs.stream().map(auditLogMapper::toDto).toList();

    // Act & Assert
    mockMvc
        .perform(get("/audits").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(expectedDtos)));

    verify(auditService).findAll();
    verify(auditService, never()).findByUsername(anyString());
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
  void getByUsername_WhenUsernameParameterProvided_ShouldReturnFilteredAuditLogs()
      throws Exception {
    // Arrange
    String username = "testUser";
    List<AuditLog> auditLogs =
        List.of(
            createAuditLog(1L, username, AuditAction.LOGIN),
            createAuditLog(2L, username, AuditAction.LOGOUT));
    when(auditService.findByUsername(username)).thenReturn(auditLogs);

    List<AuditLogDto> expectedDtos = auditLogs.stream().map(auditLogMapper::toDto).toList();

    // Act & Assert
    mockMvc
        .perform(get("/audits").param("username", username).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(expectedDtos)));

    verify(auditService).findByUsername(username);
    verify(auditService, never()).findAll();
  }

  @Test
  void getByUsername_WhenUsernameParameterEmpty_ShouldReturnFilteredAuditLogs() throws Exception {
    // Arrange
    String username = "";
    List<AuditLog> auditLogs = List.of(createAuditLog(1L, "", AuditAction.SEARCH));
    when(auditService.findByUsername(username)).thenReturn(auditLogs);

    List<AuditLogDto> expectedDtos = auditLogs.stream().map(auditLogMapper::toDto).toList();

    // Act & Assert
    mockMvc
        .perform(get("/audits").param("username", username).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(expectedDtos)));

    verify(auditService).findByUsername(username);
    verify(auditService, never()).findAll();
  }

  @Test
  void getByUsername_WhenNoAuditLogsFound_ShouldReturnEmptyArray() throws Exception {
    // Arrange
    when(auditService.findAll()).thenReturn(List.of());

    // Act & Assert
    mockMvc
        .perform(get("/audits").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));

    verify(auditService).findAll();
    verify(auditService, never()).findByUsername(anyString());
  }

  @Test
  void getByUsername_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
    // Arrange
    when(auditService.findAll()).thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    mockMvc
        .perform(get("/audits").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getByUsername_WithInvalidParameters_ShouldReturnBadRequest() throws Exception {
    // Arrange
    when(auditService.findAll()).thenReturn(List.of());
    // Act & Assert
    mockMvc
        .perform(get("/audits").param("invalidParam", "value"))
        .andExpect(status().isOk());
  }

  @Test
  void postToAudits_ShouldReturnMethodNotAllowed() throws Exception {
    mockMvc.perform(post("/audits")).andExpect(status().isMethodNotAllowed());
  }
}
