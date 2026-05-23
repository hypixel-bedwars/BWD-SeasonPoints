package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerRepository {

  private final DatabaseManager databaseManager;

  public PlayerRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public void createPlayerIfNotExists(UUID uuid, String username) {
    String sql = """
          INSERT INTO players (
              uuid,
              username
          )
          VALUES (?, ?)

          ON CONFLICT (uuid)
          DO NOTHING
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setObject(1, uuid);

      statement.setString(2, username);

      statement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }
}
