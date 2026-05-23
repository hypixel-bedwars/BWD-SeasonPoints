package dev.bwd.seasonpoints.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class CompletedAdvancement {
  private final int seasonId;
  private final UUID playerUuid;
  private final String advancementKey; 
  private final LocalDateTime completedAt;

  public CompletedAdvancement(
    int seasonId,
    UUID playerUuid,
    String advancementKey,
    LocalDateTime completedAt
  ) {
    this.seasonId = seasonId;
    this.playerUuid = playerUuid;
    this.advancementKey = advancementKey;
    this.completedAt = completedAt;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public UUID getPlayerUuid() {
    return playerUuid;
  }

  public String getAdvancementKey() {
    return advancementKey;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }
}