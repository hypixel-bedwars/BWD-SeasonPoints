package dev.bwd.seasonpoints.database.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.bwd.seasonpoints.SeasonPointsPlugin;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

  private final SeasonPointsPlugin plugin;
  private HikariDataSource dataSource;

  public DatabaseManager(SeasonPointsPlugin plugin) {
    this.plugin = plugin;
  }

  public void connect() {
    try {
        Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException exception) {
        throw new RuntimeException(exception);
    }
    
    String host = plugin.getConfig().getString("database.host");
    int port = plugin.getConfig().getInt("database.port");
    String database = plugin.getConfig().getString("database.database");
    String username = plugin.getConfig().getString("database.username");
    String password = plugin.getConfig().getString("database.password");

    HikariConfig config = new HikariConfig();

    config.setJdbcUrl(
      "jdbc:postgresql://" + host + ":" + port + "/" + database
    );

    config.setUsername(username);
    config.setPassword(password);

    config.setMaximumPoolSize(10);

    config.setPoolName("BWD-SeasonPoints");

    this.dataSource = new HikariDataSource(config);

    plugin.getLogger().info("Connected to PostgreSQL!");
  }

  public void disconnect() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
    }

    plugin.getLogger().info("Disconnected from PostgreSQL!");
  }

  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
