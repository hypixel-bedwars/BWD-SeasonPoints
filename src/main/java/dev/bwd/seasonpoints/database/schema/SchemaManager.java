package dev.bwd.seasonpoints.database.schema;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class SchemaManager {

  private final SeasonPointsPlugin plugin;

  private final DatabaseManager databaseManager;

  public SchemaManager(
    SeasonPointsPlugin plugin,
    DatabaseManager databaseManager
  ) {
    this.plugin = plugin;

    this.databaseManager = databaseManager;
  }

  public void initializeSchemas() {
    executeSchema("database/schema/001_players.sql");
    executeSchema("database/schema/002_seasons.sql");
    executeSchema("database/schema/003_biome_discoveries.sql");
    executeSchema("database/schema/004_structure_discoveries.sql");
    executeSchema("database/schema/005_advancement_rewards.sql");
    executeSchema("database/schema/006_season_points.sql");
    executeSchema("database/schema/007_pvp_kills.sql");
    executeSchema("database/schema/008_survival_stats.sql");
    executeSchema("database/schema/009_point_transactions.sql");
  }

  private void executeSchema(String resourcePath) {
    try (
      InputStream inputStream = getClass()
        .getClassLoader()
        .getResourceAsStream(resourcePath)
    ) {
      if (inputStream == null) {
        throw new RuntimeException(
          "Could not find schema file: " + resourcePath
        );
      }

      StringBuilder sqlBuilder = new StringBuilder();

      try (
        BufferedReader reader = new BufferedReader(
          new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )
      ) {
        String line;

        while ((line = reader.readLine()) != null) {
          sqlBuilder.append(line).append("\n");
        }
      }

      try (
        Connection connection = databaseManager.getConnection();
        Statement statement = connection.createStatement()
      ) {
        statement.execute(sqlBuilder.toString());

        plugin.getLogger().info("Executed schema: " + resourcePath);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
