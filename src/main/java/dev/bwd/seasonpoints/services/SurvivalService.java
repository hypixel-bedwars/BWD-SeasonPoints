package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.SurvivalRepository;
import dev.bwd.seasonpoints.models.TransactionType;

import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SurvivalService {

  private final SeasonPointsPlugin plugin;
  private final SurvivalRepository survivalRepository;
  private final PointsService pointsService;
  private final SeasonService seasonService;

  private static final Set<Material> TRACKED_ORES = Set.of(
    Material.GOLD_ORE,
    Material.DEEPSLATE_GOLD_ORE,
    Material.NETHER_GOLD_ORE,

    Material.DIAMOND_ORE,
    Material.DEEPSLATE_DIAMOND_ORE,

    Material.EMERALD_ORE,
    Material.DEEPSLATE_EMERALD_ORE,

    Material.ANCIENT_DEBRIS
  );

  public SurvivalService(
    SeasonPointsPlugin plugin,
    SurvivalRepository survivalRepository,
    PointsService pointsService,
    SeasonService seasonService
  ) {
    this.plugin = plugin;
    this.survivalRepository = survivalRepository;
    this.pointsService = pointsService;
    this.seasonService = seasonService;
  }

  public void processOreMine(Player player, Material ore) {
    if (!TRACKED_ORES.contains(ore)) {
      return;
    }

    UUID uuid = player.getUniqueId();
    int currentSeason = seasonService.getCurrentSeasonId();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      int miningCount = survivalRepository.incrementMiningAndGet(
        currentSeason,
        uuid,
        ore.name()
      );

      int basePoints = getOreValue(ore);
      int reward = calculateDiminishingReward(basePoints, miningCount);

      if (reward <= 0) {
        return;
      }

      pointsService.awardPointsAsync(currentSeason, uuid, reward, TransactionType.SURVIVAL_REWARD);
    });
  }

  public void processFishing(Player player) {
    UUID uuid = player.getUniqueId();
    int currentSeason = seasonService.getCurrentSeasonId();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      int fishingCount = survivalRepository.incrementFishingAndGet(
        currentSeason,
        uuid
      );

      int basePoints = plugin
        .getConfig()
        .getInt("points.survival.fishing.base-points", 5);

      int reward = calculateDiminishingReward(basePoints, fishingCount);

      if (reward <= 0) {
        return;
      }

      pointsService.awardPointsAsync(currentSeason, uuid, reward, TransactionType.SURVIVAL_REWARD);
    });
  }

  public void processVillagerTrade(Player player) {
    UUID uuid = player.getUniqueId();
    int currentSeason = seasonService.getCurrentSeasonId();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      int tradeCount = survivalRepository.incrementTradingAndGet(
        currentSeason,
        uuid
      );

      int basePoints = plugin
        .getConfig()
        .getInt("points.survival.villager-trading.base-points", 3);

      int reward = calculateDiminishingReward(basePoints, tradeCount);

      if (reward <= 0) {
        return;
      }

      pointsService.awardPointsAsync(currentSeason, uuid, reward, TransactionType.SURVIVAL_REWARD);
    });
  }

  private int calculateDiminishingReward(int basePoints, int count) {
    return Math.max(1, (int) (basePoints / Math.sqrt(Math.max(count, 1))));
  }

  private int getOreValue(Material ore) {
    String key = normalizeOreKey(ore);
    return plugin.getConfig().getInt("points.survival.ores." + key, 1);
  }

  private String normalizeOreKey(Material material) {
    return material
      .name()
      .toLowerCase()
      .replace("deepslate_", "")
      .replace("_ore", "");
  }

  // private String formatMaterial(Material material) {
  //   String name = material.name().toLowerCase().replace("_", " ");
  //   return Character.toUpperCase(name.charAt(0)) + name.substring(1);
  // }
}
