package org.example.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.AuditAction;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuditLogDto {
  private Long id;
  private LocalDateTime timestamp;
  private String username;
  private AuditAction action;
  private String details;
}
