package org.example.mapper;

import org.example.dto.AuditLogDto;
import org.example.model.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
  AuditLogDto toDto(AuditLog auditLog);
}
