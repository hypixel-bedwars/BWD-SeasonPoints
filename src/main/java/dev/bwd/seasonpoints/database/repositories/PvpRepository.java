package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PvpRepository {

  private final DatabaseManager dbManager;

  public PvpRepository(DatabaseManager dbManager) {
    this.dbManager = dbManager;
  }

  /**
   * Inserts a record of a player killing another player.
   * Returns true if successfully inserted, false if it conflicted (already exists).
   */
  public boolean insertUniqueKill(
    int seasonId,
    UUID killerUuid,
    UUID victimUuid
  ) {
    String sql =
      "INSERT INTO pvp_kills (season_id, killer_uuid, victim_uuid) " +
      "VALUES (?, ?, ?) ON CONFLICT DO NOTHING;";

    try (
      Connection conn = dbManager.getConnection();
      PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
      stmt.setInt(1, seasonId);
      stmt.setString(2, killerUuid.toString());
      stmt.setString(3, victimUuid.toString());

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
