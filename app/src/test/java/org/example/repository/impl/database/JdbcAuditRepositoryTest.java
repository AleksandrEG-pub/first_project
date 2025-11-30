package org.example.repository.impl.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.example.configuration.LiquibaseConfigurationUpdater;
import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    classes = {
      JdbcAuditRepository.class,
      ConnectionManager.class,
      LiquibaseConfigurationUpdater.class
    })
class JdbcAuditRepositoryTest extends BaseRepositoryTest {

  @Autowired JdbcAuditRepository auditRepository;

  @Test
  void save_ShouldPersistAuditLogAndReturnWithGeneratedId() {
    // Given
    AuditLog auditLog =
        createTestAuditLog("john.doe", AuditAction.LOGIN, "User logged in from IP 192.168.1.1");

    // When
    AuditLog savedAuditLog = auditRepository.save(auditLog);

    // Then
    assertThat(savedAuditLog).isNotNull();
    assertThat(savedAuditLog.getId()).isNotNull().isPositive();
    assertThat(savedAuditLog.getUsername()).isEqualTo("john.doe");
    assertThat(savedAuditLog.getAction()).isEqualTo(AuditAction.LOGIN);
    assertThat(savedAuditLog.getDetails()).isEqualTo("User logged in from IP 192.168.1.1");
    assertThat(savedAuditLog.getTimestamp()).isEqualTo(auditLog.getTimestamp());
  }

  private AuditLog createTestAuditLog(String username, AuditAction action, String details) {
    AuditLog auditLog = new AuditLog();
    auditLog.setTimestamp(LocalDateTime.now());
    auditLog.setUsername(username);
    auditLog.setAction(action);
    auditLog.setDetails(details);
    return auditLog;
  }

  @Test
  void findByUsername_ShouldReturnLogsForSpecificUser_OrderedByTimestampDesc()
      throws InterruptedException {
    // Given
    AuditLog log1 = createTestAuditLog("alice", AuditAction.LOGIN, "First login");
    Thread.sleep(10);
    AuditLog log2 = createTestAuditLog("bob", AuditAction.ADD_PRODUCT, "Created resource");
    Thread.sleep(10);
    AuditLog log3 = createTestAuditLog("alice", AuditAction.LOGOUT, "Logged out");

    auditRepository.save(log1);
    auditRepository.save(log2);
    auditRepository.save(log3);

    // When
    List<AuditLog> aliceLogs = auditRepository.findByUsername("alice");

    // Then
    assertThat(aliceLogs).hasSize(2);
    assertThat(aliceLogs).extracting(AuditLog::getUsername).containsOnly("alice");
    assertThat(aliceLogs)
        .extracting(AuditLog::getAction)
        .containsExactly(AuditAction.LOGOUT, AuditAction.LOGIN);

    // Verify ordering by timestamp descending
    assertThat(aliceLogs.get(0).getTimestamp()).isAfter(aliceLogs.get(1).getTimestamp());
  }

  @Test
  void findByUsername_ShouldReturnEmptyList_WhenUserHasNoLogs() {
    // Given
    auditRepository.save(createTestAuditLog("user", AuditAction.SEARCH, "Viewed page"));

    // When
    List<AuditLog> logs = auditRepository.findByUsername("non-user");

    // Then
    assertThat(logs).isEmpty();
  }

  @Test
  void integrationTest_SaveAndRetrieveMultipleUsers() {
    // Given
    AuditLog log1 = createTestAuditLog("admin", AuditAction.ADD_PRODUCT, "add product");
    AuditLog log2 = createTestAuditLog("user", AuditAction.LOGIN, "User login");
    AuditLog log3 = createTestAuditLog("admin", AuditAction.DELETE_PRODUCT, "Deleted product");

    // When
    AuditLog saved1 = auditRepository.save(log1);
    AuditLog saved2 = auditRepository.save(log2);
    AuditLog saved3 = auditRepository.save(log3);

    // Then
    List<AuditLog> allLogs = auditRepository.findAll();
    List<AuditLog> adminLogs = auditRepository.findByUsername("admin");
    List<AuditLog> userLogs = auditRepository.findByUsername("user");

    assertThat(allLogs).hasSize(3);
    assertThat(adminLogs).hasSize(2);
    assertThat(userLogs).hasSize(1);

    assertThat(saved1.getId()).isNotEqualTo(saved2.getId()).isNotEqualTo(saved3.getId());
  }

  @Test
  void findAll_ShouldReturnEmptyList_WhenNoLogsExist() {
    // When
    List<AuditLog> logs = auditRepository.findAll();

    // Then
    assertThat(logs).isEmpty();
  }
}
