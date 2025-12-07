package org.example_database.database;

import java.sql.Connection;
import java.util.function.Function;

/** Provides access methods to database */
public interface ConnectionManager {
  /**
   * Provides execution with configured database connection
   *
   * @param connectionFunction function to execute
   * @return result of execution
   * @param <T> type of the result
   */
  <T> T doInTransaction(Function<Connection, T> connectionFunction);
}
