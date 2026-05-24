package dev.bwd.seasonpoints;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.database.repositories.AdvancementRepository;
import dev.bwd.seasonpoints.database.repositories.DiscoveryRepository;
import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.database.repositories.PointsRepository;
import dev.bwd.seasonpoints.database.repositories.PvpRepository;
import dev.bwd.seasonpoints.database.repositories.SeasonRepository;
import dev.bwd.seasonpoints.database.repositories.VerificationRepository;
import dev.bwd.seasonpoints.database.schema.SchemaManager;
import dev.bwd.seasonpoints.integrations.discord.DiscordService;
import dev.bwd.seasonpoints.integrations.discord.DiscordVerificationClient;
import dev.bwd.seasonpoints.listeners.AdvancementListener;
import dev.bwd.seasonpoints.listeners.DiscoveryListener;
import dev.bwd.seasonpoints.listeners.PlayerConnectionListener;
import dev.bwd.seasonpoints.listeners.PlayerDisconnectListener;
import dev.bwd.seasonpoints.listeners.PvPListener;
import dev.bwd.seasonpoints.listeners.SurvivalListener;
import dev.bwd.seasonpoints.listeners.VerificationListener;
import dev.bwd.seasonpoints.placeholders.SeasonPointsExpansion;
import dev.bwd.seasonpoints.services.AdvancementService;
import dev.bwd.seasonpoints.services.DiscoveryService;
import dev.bwd.seasonpoints.services.PointsService;
import dev.bwd.seasonpoints.services.PvpService;
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
    PointsRepository pointsRepository = new PointsRepository(databaseManager);
    PlayerRepository playerRepository = new PlayerRepository(databaseManager);
    AdvancementRepository advancementRepository = new AdvancementRepository(
      databaseManager
    );
    VerificationRepository verificationRepository = new VerificationRepository(
      databaseManager
    );
    DiscoveryRepository discoveryRepository = new DiscoveryRepository(
      databaseManager
    );
    PvpRepository pvpRepository = new PvpRepository(databaseManager);

    // =========================================
    // 3. SERVICES (Business Logic)
    // =========================================
    SeasonService seasonService = new SeasonService(this, seasonRepository);
    seasonService.ensureCurrentSeasonExists();

    PointsService pointsService = new PointsService(this, pointsRepository);

    this.discordService = new DiscordService(this);
    this.discordService.initializeDiscordService();
    DiscordVerificationClient discordClient = new DiscordVerificationClient(
      this,
      discordService
    );

    AdvancementService advancementService = new AdvancementService(
      this,
      advancementRepository,
      pointsService
    );
    VerificationService verificationService = new VerificationService(
      verificationRepository,
      discordClient
    );

    DiscoveryService discoveryService = new DiscoveryService(
      this,
      discoveryRepository,
      pointsService
    );
    PvpService pvpService = new PvpService(
      this,
      pvpRepository,
      pointsService,
      seasonService
    );

    // =========================================
    // 4. LISTENERS (Events)
    // =========================================
    if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new SeasonPointsExpansion(this, pointsService).register();

      getLogger().info("Registered PlaceholderAPI expansion!");
    }

    // =========================================
    // 5. LISTENERS (Events)
    // =========================================
    registerListeners(
      advancementService,
      playerRepository,
      verificationService,
      pointsService,
      discoveryService,
      pvpService
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

  /**
   * Helper method to keep onEnable clean by grouping all listener registrations.
   */
  private void registerListeners(
    AdvancementService advancementService,
    PlayerRepository playerRepository,
    VerificationService verificationService,
    PointsService pointsService,
    DiscoveryService discoveryService,
    PvpService pvpService
  ) {
    PluginManager pm = getServer().getPluginManager();

    pm.registerEvents(new AdvancementListener(advancementService), this);
    pm.registerEvents(
      new PlayerConnectionListener(
        this,
        playerRepository,
        pointsService,
        discoveryService
      ),
      this
    );
    pm.registerEvents(
      new VerificationListener(this, verificationService),
      this
    );
    pm.registerEvents(new SurvivalListener(), this);
    pm.registerEvents(new PvPListener(pvpService), this);
    pm.registerEvents(new DiscoveryListener(discoveryService), this);
    pm.registerEvents(
      new PlayerDisconnectListener(
        playerRepository,
        pointsService,
        discoveryService
      ),
      this
    );
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
