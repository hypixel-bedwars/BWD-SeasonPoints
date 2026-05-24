package dev.bwd.seasonpoints.integrations.discord.helpers;

import dev.bwd.seasonpoints.models.SeasonPlayer;

public record ProfileSnapshot(
  SeasonPlayer player,
  int seasonId,
  int seasonPoints,
  int lifetimePoints,
  boolean fromCache
) {}
