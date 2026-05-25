package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.models.LeaderboardEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

  public boolean removePoints(int seasonId, UUID playerUuid, int points) {
    String updateSeasonSql = """
          UPDATE season_points
          SET points = points - ?
          WHERE season_id = ?
          AND player_uuid = ?
          AND points >= ?
      """;

    String updateLifetimeSql = """
          UPDATE players
          SET total_points = total_points - ?
          WHERE uuid = ?
          AND total_points >= ?
      """;

    try (Connection conn = databaseManager.getConnection()) {
      boolean originalAutoCommit = conn.getAutoCommit();
      conn.setAutoCommit(false);

      try (
        PreparedStatement psSeason = conn.prepareStatement(updateSeasonSql);
        PreparedStatement psLifetime = conn.prepareStatement(updateLifetimeSql)
      ) {
        psSeason.setInt(1, points);
        psSeason.setInt(2, seasonId);
        psSeason.setObject(3, playerUuid);
        psSeason.setInt(4, points);
        int seasonalUpdated = psSeason.executeUpdate();

        psLifetime.setInt(1, points);
        psLifetime.setObject(2, playerUuid);
        psLifetime.setInt(3, points);
        int lifetimeUpdated = psLifetime.executeUpdate();

        /*
         * Prevent negatives
         */

        if (seasonalUpdated == 0 || lifetimeUpdated == 0) {
          conn.rollback();

          return false;
        }

        conn.commit();

        return true;
      } catch (SQLException exception) {
        conn.rollback();

        throw exception;
      } finally {
        conn.setAutoCommit(originalAutoCommit);
      }
    } catch (SQLException exception) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to remove points from " +
          playerUuid +
          ": " +
          exception.getMessage()
      );
    }

    return false;
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

  public List<LeaderboardEntry> getTopForSeason(int seasonId, int limit) {
    String sql = """
          SELECT sp.player_uuid, p.username, sp.points
          FROM season_points sp
          INNER JOIN players p ON p.uuid = sp.player_uuid
          WHERE sp.season_id = ?
          ORDER BY sp.points DESC, p.username ASC
          LIMIT ?
      """;

    List<LeaderboardEntry> entries = new ArrayList<>();

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);
      statement.setInt(2, limit);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          entries.add(
            new LeaderboardEntry(
              resultSet.getObject("player_uuid", UUID.class),
              resultSet.getString("username"),
              resultSet.getInt("points")
            )
          );
        }
      }
    } catch (SQLException exception) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to fetch leaderboard for season " +
          seasonId +
          ": " +
          exception.getMessage()
      );
    }

    return entries;
  }

  public int getActivePlayerCount(int seasonId) {
    String sql =
      "SELECT COUNT(*) FROM season_points WHERE season_id = ? AND points > 0;";

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt(1);
        }
      }
    } catch (SQLException exception) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to count active players: " +
          exception.getMessage()
      );
    }

    return 0;
  }

  public long getTotalPointsAwarded(int seasonId) {
    String sql =
      "SELECT COALESCE(SUM(points), 0) FROM season_points WHERE season_id = ?;";

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getLong(1);
        }
      }
    } catch (SQLException exception) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to sum points: " + exception.getMessage()
      );
    }

    return 0L;
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
