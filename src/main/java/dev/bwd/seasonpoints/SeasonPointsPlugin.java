package dev.bwd.seasonpoints;

import org.bukkit.plugin.java.JavaPlugin;

public class SeasonPointsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        getLogger().info("BWD-SeasonPoints enabled!");
    }

    @Override
    public void onDisable() {

        getLogger().info("BWD-SeasonPoints disabled!");
    }
}