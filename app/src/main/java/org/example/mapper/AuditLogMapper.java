package org.example.mapper;

import org.example.dto.AuditLogDto;
import org.example.model.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuditLogMapper {
  AuditLogMapper INSTANCE = Mappers.getMapper(AuditLogMapper.class);

  AuditLogDto toDto(AuditLog auditLog);
}
