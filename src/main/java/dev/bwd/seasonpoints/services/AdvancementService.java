package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.AdvancementRepository;
import dev.bwd.seasonpoints.models.AdvancementTier;
import dev.bwd.seasonpoints.models.TransactionType;

import java.util.UUID;

public class AdvancementService {

  private final SeasonPointsPlugin plugin;
  private final AdvancementRepository advancementRepository;
  private final PointsService pointsService;

  public AdvancementService(
    SeasonPointsPlugin plugin,
    AdvancementRepository advancementRepository,
    PointsService pointsService
  ) {
    this.plugin = plugin;
    this.advancementRepository = advancementRepository;
    this.pointsService = pointsService;
  }

  public void handleAdvancement(
    UUID playerUuid,
    String advancementKey,
    AdvancementTier tier
  ) {
    int currentSeason = plugin.getConfig().getInt("season.current-season");

    boolean alreadyCompleted = advancementRepository.hasCompletedAdvancement(
      currentSeason,
      playerUuid,
      advancementKey
    );

    if (alreadyCompleted) {
      return;
    }

    advancementRepository.createAdvancement(
      currentSeason,
      playerUuid,
      advancementKey
    );

    int points = getPointsForTier(tier);

    pointsService.awardPointsAsync(currentSeason, playerUuid, points, TransactionType.ADVANCEMENT_REWARD);
  }

  private int getPointsForTier(AdvancementTier tier) {
    return switch (tier) {
      case LEGENDARY -> plugin
        .getConfig()
        .getInt("points.advancements.Legendary");
      case GRINDY -> plugin.getConfig().getInt("points.advancements.Grindy");
      case SECRET -> plugin.getConfig().getInt("points.advancements.Secret");
      case RARE -> plugin.getConfig().getInt("points.advancements.Rare");
    };
  }
}