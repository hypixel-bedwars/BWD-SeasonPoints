package dev.bwd.seasonpoints;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.database.repositories.AdvancementRepository;
import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.database.repositories.PointsRepository;
import dev.bwd.seasonpoints.database.repositories.SeasonRepository;
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
import dev.bwd.seasonpoints.services.AdvancementService;
import dev.bwd.seasonpoints.services.PointsService;
import dev.bwd.seasonpoints.services.SeasonService;
import dev.bwd.seasonpoints.services.VerificationService;
import dev.bwd.seasonpoints.utils.MessageManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SeasonPointsPlugin extends JavaPlugin {

  private DatabaseManager databaseManager;
  private MessageManager messageManager;
  private DiscordService discordService;

  @Override
  public void onEnable() {
    // =========================================
    // 1. CORE MANAGERS & CONFIG
    // =========================================
    saveDefaultConfig();
    this.messageManager = new MessageManager(this);
    
    this.databaseManager = new DatabaseManager(this);
    this.databaseManager.connect();

    SchemaManager schemaManager = new SchemaManager(this, databaseManager);
    schemaManager.initializeSchemas();

    // =========================================
    // 2. REPOSITORIES (Data Access)
    // =========================================
    SeasonRepository seasonRepository = new SeasonRepository(databaseManager);
    PointsRepository pointsRepository = new PointsRepository(databaseManager); // Added
    PlayerRepository playerRepository = new PlayerRepository(databaseManager);
    AdvancementRepository advancementRepository = new AdvancementRepository(databaseManager);
    VerificationRepository verificationRepository = new VerificationRepository(databaseManager);

    // =========================================
    // 3. SERVICES (Business Logic)
    // =========================================
    SeasonService seasonService = new SeasonService(this, seasonRepository);
    seasonService.ensureCurrentSeasonExists();

    PointsService pointsService = new PointsService(this, pointsRepository); // Added

    this.discordService = new DiscordService(this);
    this.discordService.initializeDiscordService();
    DiscordVerificationClient discordClient = new DiscordVerificationClient(this, discordService);

    AdvancementService advancementService = new AdvancementService(this, advancementRepository, pointsService); // Fixed
    VerificationService verificationService = new VerificationService(verificationRepository, discordClient);

    // =========================================
    // 4. LISTENERS (Events)
    // =========================================
    registerListeners(advancementService, playerRepository, verificationService);

    getLogger().info("BWD-SeasonPoints enabled!");
  }

  @Override
  public void onDisable() {
    if (databaseManager != null) {
      databaseManager.disconnect();
    }
    getLogger().info("BWD-SeasonPoints disabled!");
  }

  /**
   * Helper method to keep onEnable clean by grouping all listener registrations.
   */
  private void registerListeners(
    AdvancementService advancementService, 
    PlayerRepository playerRepository, 
    VerificationService verificationService
  ) {
    PluginManager pm = getServer().getPluginManager();
    
    pm.registerEvents(new AdvancementListener(advancementService), this);
    pm.registerEvents(new PlayerConnectionListener(playerRepository), this);
    pm.registerEvents(new VerificationListener(this, verificationService), this);
    pm.registerEvents(new SurvivalListener(), this);
    pm.registerEvents(new PvPListener(), this);
    pm.registerEvents(new DiscoveryListener(), this);
  }

  // --- Getters ---

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