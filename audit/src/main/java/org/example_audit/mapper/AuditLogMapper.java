package org.example_audit.mapper;

import org.example_audit.dto.AuditLogDto;
import org.example_audit.model.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
  AuditLogDto toDto(AuditLog auditLog);
}
