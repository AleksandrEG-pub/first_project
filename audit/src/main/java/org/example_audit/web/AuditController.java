package org.example_audit.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example_audit.dto.AuditLogDto;
import org.example_audit.mapper.AuditLogMapper;
import org.example_audit.service.AuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/audits")
@RestController
@RequiredArgsConstructor
public class AuditController {

  private final AuditLogMapper auditLogMapper;
  private final AuditService auditService;

  @GetMapping
  public List<AuditLogDto> getByUsername(@RequestParam(required = false) String username) {
    if (username != null) {
      return auditService.findByUsername(username).stream().map(auditLogMapper::toDto).toList();
    } else {
      return auditService.findAll().stream().map(auditLogMapper::toDto).toList();
    }
  }
}
