package dev.bwd.seasonpoints.utils;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {

  private final SeasonPointsPlugin plugin;

  private FileConfiguration messagesConfig;

  private File messagesFile;

  public MessageManager(SeasonPointsPlugin plugin) {
    this.plugin = plugin;

    load();
  }

  public void load() {
    messagesFile = new File(plugin.getDataFolder(), "messages.yml");

    if (!messagesFile.exists()) {
      plugin.saveResource("messages.yml", false);
    }

    messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
  }

  public FileConfiguration getMessagesConfig() {
    return messagesConfig;
  }
}
