package org.example_database.database;

import java.sql.Connection;
import java.util.function.Function;

public interface ConnectionManager {
  <T> T doInTransaction(Function<Connection, T> connectionFunction);
}
