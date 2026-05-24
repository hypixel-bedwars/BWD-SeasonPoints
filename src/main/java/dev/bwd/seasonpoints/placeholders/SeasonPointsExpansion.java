package dev.bwd.seasonpoints.placeholders;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.services.PointsService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeasonPointsExpansion extends PlaceholderExpansion {

  private final SeasonPointsPlugin plugin;
  private final String version;
  private final PointsService pointsService;

  public SeasonPointsExpansion(
    SeasonPointsPlugin plugin,
    PointsService pointsService
  ) {
    this.plugin = plugin;
    this.pointsService = pointsService;
    this.version = plugin.getPluginMeta().getVersion();
  }

  @Override
  public @NotNull String getIdentifier() {
    return "seasonpoints";
  }

  @Override
  public @NotNull String getAuthor() {
    return "VA80";
  }

  @Override
  public @NotNull String getVersion() {
    return version;
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @Nullable String onPlaceholderRequest(
    Player player,
    @NotNull String params
  ) {
    if (player == null) {
      return "";
    }

    if (params.equalsIgnoreCase("season_points")) {
      int currentSeason = plugin.getConfig().getInt("season.current-season");
      int points = pointsService.getSeasonPoints(
        currentSeason,
        player.getUniqueId()
      );

      return String.valueOf(points);
    }

    if (params.equalsIgnoreCase("season")) {
      return String.valueOf(plugin.getConfig().getInt("season.current-season"));
    }

    return null;
  }
}
