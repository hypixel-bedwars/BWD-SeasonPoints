package dev.bwd.seasonpoints;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SeasonPointsPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.databaseManager = new DatabaseManager(this);

        databaseManager.connect();

        getLogger().info("BWD-SeasonPoints enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("BWD-SeasonPoints disabled!");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
