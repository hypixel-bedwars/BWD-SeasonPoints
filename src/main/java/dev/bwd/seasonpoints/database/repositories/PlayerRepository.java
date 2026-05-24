package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.models.SeasonPlayer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
  
  public boolean exists(UUID uuid) {
    String sql = """
          SELECT 1
          FROM players
          WHERE uuid = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setObject(1, uuid);

      ResultSet resultSet = statement.executeQuery();

      return resultSet.next();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return false;
  }
  
  public void updateUsername(UUID uuid, String username) {
    String sql = """
          UPDATE players
          SET username = ?
          WHERE uuid = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setString(1, username);

      statement.setObject(2, uuid);

      statement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }
  
  public void updateLastSeen(UUID uuid) {
    String sql = """
          UPDATE players
          SET last_seen = NOW()
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
  
  public SeasonPlayer getPlayer(UUID uuid) {
    String sql = """
          SELECT *
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
        return new SeasonPlayer(
          resultSet.getObject("uuid", UUID.class),

          resultSet.getString("username"),

          resultSet.getString("discord_id"),

          resultSet.getInt("total_points"),

          resultSet.getTimestamp("first_joined").toLocalDateTime(),

          resultSet.getTimestamp("last_seen").toLocalDateTime()
        );
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return null;
  }

  public SeasonPlayer getPlayerByDiscordId(String discordId) {
    String sql = """
          SELECT *
          FROM players
          WHERE discord_id = ?
      """;
  
    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setString(1, discordId);
  
      ResultSet resultSet = statement.executeQuery();
  
      if (resultSet.next()) {
        return new SeasonPlayer(
          resultSet.getObject("uuid", UUID.class),
          resultSet.getString("username"),
          resultSet.getString("discord_id"),
          resultSet.getInt("total_points"),
          resultSet.getTimestamp("first_joined").toLocalDateTime(),
          resultSet.getTimestamp("last_seen").toLocalDateTime()
        );
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  
    return null;
  }
}
