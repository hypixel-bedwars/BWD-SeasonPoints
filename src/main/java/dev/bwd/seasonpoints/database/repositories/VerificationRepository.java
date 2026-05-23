package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VerificationRepository {

  private final DatabaseManager databaseManager;

  public VerificationRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public void linkDiscord(UUID uuid, String discordId) {
    String sql = """
          UPDATE players
          SET discord_id = ?
          WHERE uuid = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setString(1, discordId);
      statement.setObject(2, uuid);

      statement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  public void unlinkDiscord(UUID uuid) {
    String sql = """
          UPDATE players
          SET discord_id = NULL
          WHERE uuid = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setObject(1, uuid);

      statement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  public String getDiscordId(UUID uuid) {
    String sql = """
          SELECT discord_id
          FROM players
          WHERE uuid = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setObject(1, uuid);

      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return resultSet.getString("discord_id");
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return null;
  }

  public boolean isLinked(UUID uuid) {
    return getDiscordId(uuid) != null;
  }
}
