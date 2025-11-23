package org.example.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.AuditAction;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuditLogDto {

  private Long id;
  private LocalDateTime timestamp;

  @NotNull(message = "username can not be null")
  @NotBlank(message = "username can not be empty")
  @Length(max = 255, message = "username can not be longer than 255")
  private String username;

  @NotNull(message = "action can not be null")
  private AuditAction action;

  @NotNull(message = "details can not be null")
  @NotBlank(message = "details can not be empty")
  @Length(max = 255, message = "details can not be longer than 255")
  private String details;
}
