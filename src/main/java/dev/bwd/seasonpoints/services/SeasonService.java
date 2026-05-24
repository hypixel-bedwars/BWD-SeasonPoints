package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.SeasonRepository;

public class SeasonService {

  private final SeasonPointsPlugin plugin;
  private final SeasonRepository seasonRepository;

  public SeasonService(
    SeasonPointsPlugin plugin,
    SeasonRepository seasonRepository
  ) {
    this.plugin = plugin;
    this.seasonRepository = seasonRepository;
  }

  /**
   * Pulls the configured season ID, verifies its entry in the database,
   * and automatically creates it if it is missing.
   */
  public void ensureCurrentSeasonExists() {
    int currentSeasonId = plugin.getConfig().getInt("season.current-season", 1);

    if (!seasonRepository.seasonExists(currentSeasonId)) {
      plugin
        .getLogger()
        .info(
          "Season " +
            currentSeasonId +
            " not found in database. Auto-creating entry..."
        );

      String generatedName = "Season " + currentSeasonId;
      seasonRepository.createSeason(currentSeasonId, generatedName);

      plugin
        .getLogger()
        .info("Successfully created database record for: " + generatedName);
    } else {
      plugin
        .getLogger()
        .info("Validated database entry for Season " + currentSeasonId);
    }
  }
}
