package dev.bwd.seasonpoints;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.database.repositories.AdvancementRepository;
import dev.bwd.seasonpoints.database.repositories.DiscoveryRepository;
import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.database.repositories.PointsRepository;
import dev.bwd.seasonpoints.database.repositories.PvpRepository;
import dev.bwd.seasonpoints.database.repositories.SeasonRepository;
import dev.bwd.seasonpoints.database.repositories.SurvivalRepository;
import dev.bwd.seasonpoints.database.repositories.TransactionRepository;
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
import dev.bwd.seasonpoints.services.SurvivalService;
import dev.bwd.seasonpoints.services.VerificationService;
import dev.bwd.seasonpoints.utils.MessageManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SeasonPointsPlugin extends JavaPlugin {

  private DatabaseManager databaseManager;
  private MessageManager messageManager;
  private DiscordService discordService;
  private SeasonRepository seasonRepository;
  private PointsRepository pointsRepository;
  private PlayerRepository playerRepository;
  private AdvancementRepository advancementRepository;
  private VerificationRepository verificationRepository;
  private DiscoveryRepository discoveryRepository;
  private PvpRepository pvpRepository;
  private SurvivalRepository survivalRepository;
  private TransactionRepository transactionRepository;

  private SeasonService seasonService;
  private PointsService pointsService;
  private AdvancementService advancementService;
  private VerificationService verificationService;
  private DiscoveryService discoveryService;
  private PvpService pvpService;
  private SurvivalService survivalService;

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
    this.seasonRepository = new SeasonRepository(databaseManager);
    this.pointsRepository = new PointsRepository(databaseManager);
    this.playerRepository = new PlayerRepository(databaseManager);
    this.advancementRepository = new AdvancementRepository(databaseManager);
    this.verificationRepository = new VerificationRepository(databaseManager);
    this.discoveryRepository = new DiscoveryRepository(databaseManager);
    this.pvpRepository = new PvpRepository(databaseManager);
    this.survivalRepository = new SurvivalRepository(databaseManager);
    this.transactionRepository = new TransactionRepository(databaseManager);
    
    // =========================================
    // 3. SERVICES (Business Logic)
    // =========================================
    this.seasonService = new SeasonService(this, seasonRepository);
    this.seasonService.ensureCurrentSeasonExists();

    this.pointsService = new PointsService(this, pointsRepository, transactionRepository);

    this.discordService = new DiscordService(this);
    this.discordService.initializeDiscordService();
    DiscordVerificationClient discordClient = new DiscordVerificationClient(
      this,
      discordService
    );

    this.advancementService = new AdvancementService(
      this,
      advancementRepository,
      pointsService
    );
    this.verificationService = new VerificationService(
      verificationRepository,
      discordClient
    );

    this.discoveryService = new DiscoveryService(
      this,
      discoveryRepository,
      pointsService
    );
    this.pvpService = new PvpService(
      this,
      pvpRepository,
      pointsService,
      seasonService
    );
    this.survivalService = new SurvivalService(
      this,
      survivalRepository,
      pointsService,
      seasonService
    );

    // =========================================
    // 4. PLACEHOLDERS / INTEGRATIONS
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
      pvpService,
      survivalService
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
    PvpService pvpService,
    SurvivalService survivalService
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
    pm.registerEvents(new SurvivalListener(survivalService), this);
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

  public SeasonRepository getSeasonRepository() {
    return seasonRepository;
  }

  public PointsRepository getPointsRepository() {
    return pointsRepository;
  }

  public PlayerRepository getPlayerRepository() {
    return playerRepository;
  }

  public AdvancementRepository getAdvancementRepository() {
    return advancementRepository;
  }

  public VerificationRepository getVerificationRepository() {
    return verificationRepository;
  }

  public DiscoveryRepository getDiscoveryRepository() {
    return discoveryRepository;
  }

  public PvpRepository getPvpRepository() {
    return pvpRepository;
  }

  public SurvivalRepository getSurvivalRepository() {
    return survivalRepository;
  }

  public SeasonService getSeasonService() {
    return seasonService;
  }

  public PointsService getPointsService() {
    return pointsService;
  }

  public AdvancementService getAdvancementService() {
    return advancementService;
  }

  public VerificationService getVerificationService() {
    return verificationService;
  }

  public DiscoveryService getDiscoveryService() {
    return discoveryService;
  }

  public PvpService getPvpService() {
    return pvpService;
  }

  public SurvivalService getSurvivalService() {
    return survivalService;
  }
}