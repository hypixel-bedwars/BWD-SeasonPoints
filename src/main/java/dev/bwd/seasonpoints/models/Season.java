package dev.bwd.seasonpoints.models;

import java.time.LocalDateTime;

public record Season(
  int id,
  String name,
  LocalDateTime startedAt,
  LocalDateTime endedAt
) {
  public boolean isActive() {
    return endedAt == null;
  }
}
