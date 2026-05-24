package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.services.DiscoveryService;
import dev.bwd.seasonpoints.services.PointsService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionListener implements Listener {

  private final PlayerRepository playerRepository;
  private final PointsService pointsService;
  private final DiscoveryService discoveryService;
  private final SeasonPointsPlugin plugin;

  public PlayerConnectionListener(
    SeasonPointsPlugin plugin,
    PlayerRepository playerRepository,
    PointsService pointsService,
    DiscoveryService discoveryService
  ) {
    this.plugin = plugin;
    this.playerRepository = playerRepository;
    this.pointsService = pointsService;
    this.discoveryService = discoveryService;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    playerRepository.createPlayerIfNotExists(
      player.getUniqueId(),
      player.getName()
    );

    int currentSeason = plugin.getConfig().getInt("season.current-season");

    pointsService.loadPlayerCache(currentSeason, player.getUniqueId());

    discoveryService.loadPlayerCache(currentSeason, player.getUniqueId());
  }
}
