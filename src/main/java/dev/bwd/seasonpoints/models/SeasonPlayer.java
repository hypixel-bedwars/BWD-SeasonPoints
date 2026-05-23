package dev.bwd.seasonpoints.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class SeasonPlayer {

  private final UUID uuid;

  private final String username;

  private final String discordId;

  private final int totalPoints;

  private final LocalDateTime firstJoined;

  private final LocalDateTime lastSeen;

  public SeasonPlayer(
    UUID uuid,
    String username,
    String discordId,
    int totalPoints,
    LocalDateTime firstJoined,
    LocalDateTime lastSeen
  ) {
    this.uuid = uuid;

    this.username = username;

    this.discordId = discordId;

    this.totalPoints = totalPoints;

    this.firstJoined = firstJoined;

    this.lastSeen = lastSeen;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getUsername() {
    return username;
  }

  public String getDiscordId() {
    return discordId;
  }

  public int getTotalPoints() {
    return totalPoints;
  }

  public LocalDateTime getFirstJoined() {
    return firstJoined;
  }

  public LocalDateTime getLastSeen() {
    return lastSeen;
  }
}
