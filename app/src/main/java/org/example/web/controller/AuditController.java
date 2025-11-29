package org.example.web.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.AuditLogDto;
import org.example.mapper.AuditLogMapper;
import org.example.service.AuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/audits")
@RestController
@RequiredArgsConstructor
public class AuditController {

  private static final AuditLogMapper AUDIT_LOG_MAPPER = AuditLogMapper.INSTANCE;
  private final transient AuditService auditService;

  @GetMapping
  public List<AuditLogDto> getByUsername(@RequestParam(required = false) String username) {
    if (username != null) {
      return auditService.findByUsername(username).stream().map(AUDIT_LOG_MAPPER::toDto).toList();
    } else{
      return auditService.findAll().stream().map(AUDIT_LOG_MAPPER::toDto).toList();
    }
  }
}
