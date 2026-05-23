package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.models.DiscoveredBiome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscoveryRepository {

  private final DatabaseManager databaseManager;

  public DiscoveryRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public boolean hasDiscoveredBiome(
    int seasonId,
    UUID playerUuid,
    String biomeKey
  ) {
    String sql = """
          SELECT 1
          FROM biome_discoveries
          WHERE season_id = ?
          AND player_uuid = ?
          AND biome_key = ?
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      statement.setObject(2, playerUuid);

      statement.setString(3, biomeKey);

      ResultSet resultSet = statement.executeQuery();

      return resultSet.next();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return false;
  }

  public void createBiomeDiscovery(
    int seasonId,
    UUID playerUuid,
    String biomeKey
  ) {
    String sql = """
          INSERT INTO biome_discoveries (
              season_id,
              player_uuid,
              biome_key
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

      statement.setString(3, biomeKey);

      statement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  public List<DiscoveredBiome> getBiomeDiscoveries(
    int seasonId,
    UUID playerUuid
  ) {
    List<DiscoveredBiome> discoveries = new ArrayList<>();

    String sql = """
          SELECT biome_key
          FROM biome_discoveries
          WHERE season_id = ?
          AND player_uuid = ?
          ORDER BY discovered_at ASC
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      statement.setObject(2, playerUuid);

      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        discoveries.add(
          new DiscoveredBiome(
            resultSet.getInt("season_id"),

            resultSet.getObject("player_uuid", UUID.class),

            resultSet.getString("biome_key"),

            resultSet.getTimestamp("discovered_at").toLocalDateTime()
          )
        );
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return discoveries;
  }
}
