package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.models.Season;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.bukkit.Bukkit;

public class SeasonRepository {

  private final DatabaseManager databaseManager;

  public SeasonRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public boolean seasonExists(int seasonId) {
    String sql = "SELECT 1 FROM seasons WHERE id = ?;";

    try (
      Connection conn = databaseManager.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setInt(1, seasonId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      Bukkit.getLogger().severe("[SeasonPoints] Failed to check if season exists: " + e.getMessage());
      return false;
    }
  }

  public Season getSeason(int seasonId) {
    String sql =
      "SELECT id, name, started_at, ended_at FROM seasons WHERE id = ?;";

    try (
      Connection conn = databaseManager.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setInt(1, seasonId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Timestamp endedAt = rs.getTimestamp("ended_at");
          return new Season(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getTimestamp("started_at").toLocalDateTime(),
            endedAt != null ? endedAt.toLocalDateTime() : null
          );
        }
      }
    } catch (SQLException e) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to fetch season " + seasonId + ": " + e.getMessage()
      );
    }

    return null;
  }

  public void createSeason(int seasonId, String seasonName) {
    String sql =
      "INSERT INTO seasons (id, name, started_at) VALUES (?, ?, NOW()) ON CONFLICT (id) DO NOTHING;";

    try (
      Connection conn = databaseManager.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setInt(1, seasonId);
      ps.setString(2, seasonName);
      ps.executeUpdate();
    } catch (SQLException e) {
      Bukkit.getLogger().severe(
        "[SeasonPoints] Failed to auto-create season " + seasonId + ": " + e.getMessage()
      );
    }
  }
}