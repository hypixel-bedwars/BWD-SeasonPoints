package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.DiscoveryRepository;
import dev.bwd.seasonpoints.models.DiscoveredBiome;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class DiscoveryService {

  private final SeasonPointsPlugin plugin;
  private final DiscoveryRepository discoveryRepository;
  private final PointsService pointsService;

  private final ConcurrentHashMap<UUID, Set<String>> playerDiscoveriesCache =
    new ConcurrentHashMap<>();

  private final Set<String> RARE_BIOMES = Set.of(
    "MUSHROOM_FIELDS",
    "DEEP_DARK",
    "ICE_SPIKES",
    "ERODED_BADLANDS",
    "PALE_GARDEN",
    "MODIFIED_JUNGLE",
    "WINDSWEPT_SAVANNA"
  );

  public DiscoveryService(
    SeasonPointsPlugin plugin,
    DiscoveryRepository discoveryRepository,
    PointsService pointsService
  ) {
    this.plugin = plugin;
    this.discoveryRepository = discoveryRepository;
    this.pointsService = pointsService;
  }

  /**
   * Called when a player joins the server to load their discoveries into memory.
   */
  public void loadPlayerCache(int seasonId, UUID playerUuid) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Set<String> discoveredBiomes = ConcurrentHashMap.newKeySet();

      for (DiscoveredBiome db : discoveryRepository.getBiomeDiscoveries(
        seasonId,
        playerUuid
      )) {
        discoveredBiomes.add(db.getBiomeKey());
      }

      playerDiscoveriesCache.put(playerUuid, discoveredBiomes);
      plugin
        .getLogger()
        .info(
          "[DiscoveryService] Loaded " +
            discoveredBiomes.size() +
            " biomes for " +
            playerUuid
        );
    });
  }

  /**
   * Called when a player quits to free up memory.
   */
  public void unloadPlayerCache(UUID playerUuid) {
    playerDiscoveriesCache.remove(playerUuid);
  }

  /**
   * Checks if a biome is newly discovered, awards points, and saves it.
   */
  public void handleBiomeDiscovery(Player player, Biome biome) {
    UUID uuid = player.getUniqueId();
    String biomeKey = biome.name();
    int seasonId = plugin.getConfig().getInt("season.current-season");

    Set<String> discovered = playerDiscoveriesCache.get(uuid);
    if (discovered == null) return;

    if (discovered.contains(biomeKey)) {
      return;
    }

    discovered.add(biomeKey);

    boolean isRare = RARE_BIOMES.contains(biomeKey);
    int pointsToAward = isRare
      ? plugin.getConfig().getInt("points.discoveries.rare-biome", 50)
      : plugin.getConfig().getInt("points.discoveries.common-biome", 10);

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      discoveryRepository.createBiomeDiscovery(seasonId, uuid, biomeKey);
      pointsService.awardPointsAsync(seasonId, uuid, pointsToAward);
    });
  }
}
