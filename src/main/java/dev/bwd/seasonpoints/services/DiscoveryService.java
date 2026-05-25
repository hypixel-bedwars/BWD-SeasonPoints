package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.DiscoveryRepository;
import dev.bwd.seasonpoints.models.DiscoveredBiome;
import dev.bwd.seasonpoints.models.DiscoveredStructure;
import dev.bwd.seasonpoints.models.TransactionType;

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

  public void loadPlayerCache(int seasonId, UUID playerUuid) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Set<String> discovered = ConcurrentHashMap.newKeySet();

      // Load Biomes
      for (DiscoveredBiome db : discoveryRepository.getBiomeDiscoveries(
        seasonId,
        playerUuid
      )) {
        discovered.add(db.getBiomeKey());
      }

      // Load Structures
      for (DiscoveredStructure ds : discoveryRepository.getStructureDiscoveries(
        seasonId,
        playerUuid
      )) {
        discovered.add(ds.getStructureKey());
      }

      playerDiscoveriesCache.put(playerUuid, discovered);
      plugin
        .getLogger()
        .info(
          "[DiscoveryService] Loaded " +
            discovered.size() +
            " discoveries for " +
            playerUuid
        );
    });
  }

  public void unloadPlayerCache(UUID playerUuid) {
    playerDiscoveriesCache.remove(playerUuid);
  }

  public void handleBiomeDiscovery(Player player, Biome biome) {
    UUID uuid = player.getUniqueId();
    String biomeKey = biome.name();

    Set<String> discovered = playerDiscoveriesCache.get(uuid);
    if (discovered == null || !discovered.add(biomeKey)) {
      return;
    }

    boolean isRare = RARE_BIOMES.contains(biomeKey);
    int pointsToAward = isRare
      ? plugin.getConfig().getInt("points.discoveries.rare-biome", 50)
      : plugin.getConfig().getInt("points.discoveries.common-biome", 10);

    int seasonId = plugin.getConfig().getInt("season.current-season");
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      discoveryRepository.createBiomeDiscovery(seasonId, uuid, biomeKey);
      pointsService.awardPointsAsync(seasonId, uuid, pointsToAward, TransactionType.DISCOVERY_REWARD);
    });
  }

  public void handleStructureDiscovery(Player player, String structureKey) {
    UUID uuid = player.getUniqueId();

    Set<String> discovered = playerDiscoveriesCache.get(uuid);
    if (discovered == null || !discovered.add(structureKey)) {
      return;
    }

    int pointsToAward = plugin
      .getConfig()
      .getInt("points.discoveries.structure", 25);

    int seasonId = plugin.getConfig().getInt("season.current-season");
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      discoveryRepository.createStructureDiscovery(
        seasonId,
        uuid,
        structureKey
      );
      pointsService.awardPointsAsync(seasonId, uuid, pointsToAward, TransactionType.DISCOVERY_REWARD);
    });
  }
}
