package org.example.aspect;

/** Provides list of possible variations in ways of processing audit actions */
public enum AuditType {
  /** Basic message formatting */
  SIMPLE,
  /** Handles search criteria and result counts */
  SEARCH,
  /** Handles view operations with optional results */
  VIEW,
  /** Extracts ID from args or result */
  ID_BASED
}
