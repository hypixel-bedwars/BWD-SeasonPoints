package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.PointsRepository;
import java.util.UUID;
import org.bukkit.Bukkit;

public class PointsService {

  private final SeasonPointsPlugin plugin;
  private final PointsRepository seasonPointsRepository;

  public PointsService(
    SeasonPointsPlugin plugin,
    PointsRepository seasonPointsRepository
  ) {
    this.plugin = plugin;
    this.seasonPointsRepository = seasonPointsRepository;
  }

  /**
   * Awards points to a player safely on an asynchronous thread.
   */
  public final void awardPointsAsync(
    int seasonId,
    UUID playerUuid,
    int points
  ) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      seasonPointsRepository.addPoints(seasonId, playerUuid, points);
    });
  }
}
