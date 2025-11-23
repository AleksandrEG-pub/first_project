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

  @NotNull
  @NotBlank
  @Length(max = 255)
  private Long id;
  private LocalDateTime timestamp;

  @NotNull
  @NotBlank
  @Length(max = 255)
  private String username;

  @NotNull
  private AuditAction action;

  @NotNull
  @NotBlank
  @Length(max = 255)
  private String details;
}
