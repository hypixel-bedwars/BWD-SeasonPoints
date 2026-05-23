package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.models.CompletedAdvancement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdvancementRepository {

  private final DatabaseManager databaseManager;

  public AdvancementRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public boolean hasCompletedAdvancement(
    int seasonId,
    UUID playerUuid,
    String advancementKey
  ) {
    String sql = """
          SELECT 1
          FROM advancement_rewards
          WHERE season_id = ?
          AND player_uuid = ?
          AND advancement_key = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      statement.setObject(2, playerUuid);

      statement.setString(3, advancementKey);

      ResultSet resultSet = statement.executeQuery();

      return resultSet.next();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return false;
  }

  public void createAdvancement(
    int seasonId,
    UUID playerUuid,
    String advancementKey
  ) {
    String sql = """
          INSERT INTO advancement_rewards (
              season_id,
              player_uuid,
              advancement_key
          )
          VALUES (?, ?, ?)

          ON CONFLICT DO NOTHING
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      statement.setObject(2, playerUuid);

      statement.setString(3, advancementKey);

      statement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  public List<CompletedAdvancement> getCompletedAdvancementsForPlayer(
    int seasonId,
    UUID playerUuid
  ) {
    List<CompletedAdvancement> completedAdvancements = new ArrayList<>();

    String sql = """
          SELECT *
          FROM advancement_rewards
          WHERE season_id = ?
          AND player_uuid = ?
          ORDER BY completed_at ASC
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      statement.setObject(2, playerUuid);

      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        completedAdvancements.add(
          new CompletedAdvancement(
            resultSet.getInt("season_id"),

            resultSet.getObject("player_uuid", UUID.class),

            resultSet.getString("advancement_key"),

            resultSet.getTimestamp("completed_at").toLocalDateTime()
          )
        );
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return completedAdvancements;
  }
}
