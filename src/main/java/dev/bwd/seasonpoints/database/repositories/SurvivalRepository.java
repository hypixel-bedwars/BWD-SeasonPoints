package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SurvivalRepository {

  private final DatabaseManager databaseManager;

  public SurvivalRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public int incrementMiningAndGet(
    int seasonId,
    UUID playerUuid,
    String material
  ) {
    String sql = """
          INSERT INTO survival_mining_stats (
              season_id,
              player_uuid,
              material,
              mining_count
          )
          VALUES (?, ?, ?, 1)
          ON CONFLICT (season_id, player_uuid, material)
          DO UPDATE SET
              mining_count = survival_mining_stats.mining_count + 1,
              last_mined_time = CURRENT_TIMESTAMP
          RETURNING mining_count;
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);
      statement.setObject(2, playerUuid);
      statement.setString(3, material.toUpperCase());

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt("mining_count");
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return 0;
  }

  public int incrementFishingAndGet(int seasonId, UUID playerUuid) {
    String sql = """
          INSERT INTO survival_stats (season_id, player_uuid, fishing_count)
          VALUES (?, ?, 1)
          ON CONFLICT (season_id, player_uuid)
          DO UPDATE SET fishing_count = survival_stats.fishing_count + 1
          RETURNING fishing_count;
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);
      statement.setObject(2, playerUuid);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt("fishing_count");
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return 0;
  }

  public int incrementTradingAndGet(int seasonId, UUID playerUuid) {
    String sql = """
          INSERT INTO survival_stats (season_id, player_uuid, villager_trade_count)
          VALUES (?, ?, 1)
          ON CONFLICT (season_id, player_uuid)
          DO UPDATE SET villager_trade_count = survival_stats.villager_trade_count + 1
          RETURNING villager_trade_count;
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);
      statement.setObject(2, playerUuid);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt("villager_trade_count");
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return 0;
  }
}
