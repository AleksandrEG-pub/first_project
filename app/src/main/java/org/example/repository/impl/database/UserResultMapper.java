package org.example.repository.impl.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.Role;
import org.example.model.User;

public class UserResultMapper {
  public User mapToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setUsername(rs.getString("username"));
    user.setPasswordHash(rs.getString("password_hash"));
    user.setRole(Role.valueOf(rs.getString("role")));
    return user;
  }
}
