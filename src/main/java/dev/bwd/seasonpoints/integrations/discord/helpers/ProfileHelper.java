package dev.bwd.seasonpoints.integrations.discord.helpers;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.models.SeasonPlayer;

public class ProfileHelper {
  private final SeasonPointsPlugin plugin;

  public ProfileHelper(SeasonPointsPlugin plugin) {
    this.plugin = plugin;
  }

  public int getSeasonPoints(String discordId) {
    SeasonPlayer player = plugin.getPlayerRepository().getPlayerByDiscordId(discordId);
    int season_points = plugin.getPointsService().getCachedSeasonPoints(player.getUuid());
    
    return season_points;
  }

  public int getTotalPoints(String discordId) {
    SeasonPlayer player = plugin.getPlayerRepository().getPlayerByDiscordId(discordId);
    int total_points = plugin.getPointsService().getLifetimePoints(player.getUuid());
    
    return total_points;
  }
}