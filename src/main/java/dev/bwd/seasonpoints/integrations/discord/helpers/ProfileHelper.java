package dev.bwd.seasonpoints.integrations.discord.helpers;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.models.SeasonPlayer;
import dev.bwd.seasonpoints.services.PointsService;
import java.util.UUID;
import org.bukkit.Bukkit;

public class ProfileHelper {

  private final SeasonPointsPlugin plugin;

  public ProfileHelper(SeasonPointsPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Resolves a Discord user's full profile. When the player is online we read
   * season points from the in-memory cache; otherwise we fall back to a DB read.
   * Returns {@code null} when the Discord ID is not linked to a Minecraft player.
   */
  public ProfileSnapshot getProfile(String discordId) {
    SeasonPlayer player = plugin
      .getPlayerRepository()
      .getPlayerByDiscordId(discordId);

    if (player == null) {
      return null;
    }

    UUID uuid = player.getUuid();
    int seasonId = plugin.getSeasonService().getCurrentSeasonId();
    PointsService points = plugin.getPointsService();

    boolean online = Bukkit.getPlayer(uuid) != null;
    int seasonPoints = online
      ? points.getCachedSeasonPoints(uuid)
      : points.getSeasonPoints(seasonId, uuid);

    return new ProfileSnapshot(
      player,
      seasonId,
      seasonPoints,
      player.getTotalPoints(),
      online
    );
  }
}
