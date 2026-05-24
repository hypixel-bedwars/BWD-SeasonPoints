package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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