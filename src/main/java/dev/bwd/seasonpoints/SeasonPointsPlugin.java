package dev.bwd.seasonpoints;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.database.repositories.VerificationRepository;
import dev.bwd.seasonpoints.database.schema.SchemaManager;
import dev.bwd.seasonpoints.integrations.discord.DiscordService;
import dev.bwd.seasonpoints.integrations.discord.DiscordVerificationClient;
import dev.bwd.seasonpoints.listeners.AdvancementListener;
import dev.bwd.seasonpoints.listeners.DiscoveryListener;
import dev.bwd.seasonpoints.listeners.PlayerConnectionListener;
import dev.bwd.seasonpoints.listeners.PvPListener;
import dev.bwd.seasonpoints.listeners.SurvivalListener;
import dev.bwd.seasonpoints.listeners.VerificationListener;
import dev.bwd.seasonpoints.services.VerificationService;
import dev.bwd.seasonpoints.utils.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SeasonPointsPlugin extends JavaPlugin {

  private DatabaseManager databaseManager;
  private MessageManager messageManager;
  private DiscordService discordService;

  @Override
  public void onEnable() {
    saveDefaultConfig();

    this.messageManager = new MessageManager(this);
    this.databaseManager = new DatabaseManager(this);
    databaseManager.connect();

    SchemaManager schemaManager = new SchemaManager(this, databaseManager);

    schemaManager.initializeSchemas();

    PlayerRepository playerRepository = new PlayerRepository(databaseManager);

    this.discordService = new DiscordService(this);

    discordService.initializeDiscordService();

    DiscordVerificationClient discordClient = new DiscordVerificationClient(
      this,
      discordService
    );

    getServer()
      .getPluginManager()
      .registerEvents(new AdvancementListener(), this);

    getServer().getPluginManager().registerEvents(new SurvivalListener(), this);

    getServer().getPluginManager().registerEvents(new PvPListener(), this);

    getServer()
      .getPluginManager()
      .registerEvents(new DiscoveryListener(), this);

    getServer()
      .getPluginManager()
      .registerEvents(new PlayerConnectionListener(playerRepository), this);

    VerificationRepository verificationRepository = new VerificationRepository(
      databaseManager
    );

    VerificationService verificationService = new VerificationService(
      verificationRepository,
      discordClient
    );

    getServer()
      .getPluginManager()
      .registerEvents(
        new VerificationListener(this, verificationService),
        this
      );

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

  public MessageManager getMessageManager() {
    return messageManager;
  }

  public DiscordService getDiscordService() {
    return discordService;
  }
}
