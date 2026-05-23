package dev.bwd.seasonpoints.models;

import java.util.UUID;

public class PlayerPoints {

  private final int seasonId;

  private final UUID playerUuid;

  private final int points;

  public PlayerPoints(int seasonId, UUID playerUuid, int points) {
    this.seasonId = seasonId;
    this.playerUuid = playerUuid;
    this.points = points;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public UUID getPlayerUuid() {
    return playerUuid;
  }

  public int getPoints() {
    return points;
  }
}
