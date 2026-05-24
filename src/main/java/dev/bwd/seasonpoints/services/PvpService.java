package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.PvpRepository;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PvpService {

  private final SeasonPointsPlugin plugin;
  private final PvpRepository pvpRepository;
  private final PointsService pointsService;
  private final SeasonService seasonService;

  private final ConcurrentHashMap<UUID, Set<UUID>> killCache =
    new ConcurrentHashMap<>();

  public PvpService(
    SeasonPointsPlugin plugin,
    PvpRepository pvpRepository,
    PointsService pointsService,
    SeasonService seasonService
  ) {
    this.plugin = plugin;
    this.pvpRepository = pvpRepository;
    this.pointsService = pointsService;
    this.seasonService = seasonService;
  }

  public void processPlayerKill(
    UUID killerUuid,
    UUID victimUuid,
    ItemStack weapon
  ) {
    int currentSeasonId = seasonService.getCurrentSeasonId();

    Set<UUID> claimedVictims = killCache.computeIfAbsent(killerUuid, k ->
      ConcurrentHashMap.newKeySet()
    );

    if (!claimedVictims.add(victimUuid)) {
      return;
    }

    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      boolean isUnique = pvpRepository.insertUniqueKill(
        currentSeasonId,
        killerUuid,
        victimUuid
      );

      if (isUnique) {
        boolean maceKill = weapon != null && weapon.getType() == Material.MACE;

        int pointsForPvp = maceKill
          ? plugin.getConfig().getInt("points.pvp.unique-mace-kill", 50)
          : plugin.getConfig().getInt("points.pvp.unique-kill", 10);

        pointsService.awardPointsAsync(
          currentSeasonId,
          killerUuid,
          pointsForPvp
        );
      }
    });
  }

  public void loadPlayerKillCache(UUID killerUuid, Set<UUID> victimsFromDb) {
    killCache.put(killerUuid, ConcurrentHashMap.newKeySet());
    killCache.get(killerUuid).addAll(victimsFromDb);
  }

  public void clearPlayerKillCache(UUID killerUuid) {
    killCache.remove(killerUuid);
  }
}
