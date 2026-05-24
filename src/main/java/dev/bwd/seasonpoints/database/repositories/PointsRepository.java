package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.Bukkit;

public class PointsRepository {

  private final DatabaseManager databaseManager;

  public PointsRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  /**
   * Increments both seasonal points and global lifetime points for a player.
   * Uses a database transaction to ensure both updates succeed together or fail together.
   */
  public void addPoints(int seasonId, UUID playerUuid, int points) {
    String upsertSeasonalPointsSql =
      "INSERT INTO season_points (season_id, player_uuid, points) VALUES (?, ?, ?) " +
      "ON CONFLICT (season_id, player_uuid) DO UPDATE SET points = season_points.points + EXCLUDED.points;";

    String updateLifetimePointsSql =
      "UPDATE players SET total_points = total_points + ? WHERE uuid = ?;";

    try (Connection conn = databaseManager.getConnection()) {
      boolean originalAutoCommit = conn.getAutoCommit();
      conn.setAutoCommit(false);

      try (
        PreparedStatement psSeasonal = conn.prepareStatement(
          upsertSeasonalPointsSql
        );
        PreparedStatement psLifetime = conn.prepareStatement(
          updateLifetimePointsSql
        )
      ) {
        psSeasonal.setInt(1, seasonId);
        psSeasonal.setObject(2, playerUuid);
        psSeasonal.setInt(3, points);
        psSeasonal.executeUpdate();

        psLifetime.setInt(1, points);
        psLifetime.setObject(2, playerUuid);
        psLifetime.executeUpdate();

        conn.commit();
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(originalAutoCommit);
      }
    } catch (SQLException e) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to award points to player " +
          playerUuid +
          ": " +
          e.getMessage()
      );
    }
  }

  public int getSeasonPoints(int seasonId, UUID playerUuid) {
    String sql = """
          SELECT points
          FROM season_points
          WHERE season_id = ?
          AND player_uuid = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      statement.setObject(2, playerUuid);

      var resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return resultSet.getInt("points");
      }
    } catch (SQLException exception) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to fetch season points for " +
          playerUuid +
          ": " +
          exception.getMessage()
      );
    }

    return 0;
  }

  public int getLifetimePoints(UUID playerUuid) {
    String sql = """
          SELECT total_points
          FROM players
          WHERE uuid = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setObject(1, playerUuid);

      var resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return resultSet.getInt("total_points");
      }
    } catch (SQLException exception) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to fetch lifetime points for " +
          playerUuid +
          ": " +
          exception.getMessage()
      );
    }

    return 0;
  }
}
