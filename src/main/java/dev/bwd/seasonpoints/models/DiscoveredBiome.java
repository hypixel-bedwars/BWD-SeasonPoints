package dev.bwd.seasonpoints.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiscoveredBiome {

  private final int seasonId;

  private final UUID playerUuid;

  private final String biomeKey;

  private final LocalDateTime discoveredAt;

  public DiscoveredBiome(
    int seasonId,
    UUID playerUuid,
    String biomeKey,
    LocalDateTime discoveredAt
  ) {
    this.seasonId = seasonId;
    this.playerUuid = playerUuid;
    this.biomeKey = biomeKey;
    this.discoveredAt = discoveredAt;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public UUID getPlayerUuid() {
    return playerUuid;
  }

  public String getBiomeKey() {
    return biomeKey;
  }

  public LocalDateTime getDiscoveredAt() {
    return discoveredAt;
  }
}
