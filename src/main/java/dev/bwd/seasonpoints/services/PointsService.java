package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.PointsRepository;
import dev.bwd.seasonpoints.database.repositories.TransactionRepository;
import dev.bwd.seasonpoints.models.TransactionType;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;

public class PointsService {

  private final SeasonPointsPlugin plugin;
  private final PointsRepository seasonPointsRepository;
  private final TransactionRepository transactionRepository;
  private final Map<UUID, Integer> cachedSeasonPoints =
    new ConcurrentHashMap<>();

  public PointsService(
    SeasonPointsPlugin plugin,
    PointsRepository seasonPointsRepository,
    TransactionRepository transactionRepository
  ) {
    this.plugin = plugin;
    this.seasonPointsRepository = seasonPointsRepository;
    this.transactionRepository = transactionRepository;
  }

  public int getSeasonPoints(int seasonId, UUID playerUuid) {
    int points = seasonPointsRepository.getSeasonPoints(seasonId, playerUuid);

    plugin
      .getLogger()
      .info(
        "[PointsService] DB season points for " + playerUuid + " = " + points
      );

    return points;
  }

  public int getLifetimePoints(UUID playerUuid) {
    int points = seasonPointsRepository.getLifetimePoints(playerUuid);

    plugin
      .getLogger()
      .info(
        "[PointsService] Lifetime points for " + playerUuid + " = " + points
      );

    return points;
  }

  public void loadPlayerCache(int seasonId, UUID playerUuid) {
    int points = seasonPointsRepository.getSeasonPoints(seasonId, playerUuid);

    cachedSeasonPoints.put(playerUuid, points);

    plugin
      .getLogger()
      .info(
        "[PointsService] Loaded cache for " +
          playerUuid +
          " with " +
          points +
          " points"
      );
  }

  public void unloadPlayerCache(UUID playerUuid) {
    cachedSeasonPoints.remove(playerUuid);

    plugin.getLogger().info("[PointsService] Unloaded cache for " + playerUuid);
  }

  public int getCachedSeasonPoints(UUID playerUuid) {
    int points = cachedSeasonPoints.getOrDefault(playerUuid, 0);

    return points;
  }

  /**
   * Awards points asynchronously.
   */
  public final void awardPointsAsync(
    int seasonId,
    UUID playerUuid,
    int points,
    TransactionType transactionType
  ) {
    cachedSeasonPoints.merge(playerUuid, points, Integer::sum);

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      seasonPointsRepository.addPoints(seasonId, playerUuid, points);
      transactionRepository.createTransaction(
        seasonId,
        null,
        playerUuid,
        points,
        transactionType
      );
    });
  }
}
