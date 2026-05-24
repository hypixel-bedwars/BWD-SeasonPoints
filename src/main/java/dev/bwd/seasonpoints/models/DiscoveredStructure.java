package dev.bwd.seasonpoints.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiscoveredStructure {

  private final int seasonId;
  private final UUID playerUuid;
  private final String structureKey;
  private final LocalDateTime discoveredAt;

  public DiscoveredStructure(
    int seasonId,
    UUID playerUuid,
    String structureKey,
    LocalDateTime discoveredAt
  ) {
    this.seasonId = seasonId;
    this.playerUuid = playerUuid;
    this.structureKey = structureKey;
    this.discoveredAt = discoveredAt;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public UUID getPlayerUuid() {
    return playerUuid;
  }

  public String getStructureKey() {
    return structureKey;
  }

  public LocalDateTime getDiscoveredAt() {
    return discoveredAt;
  }
}
