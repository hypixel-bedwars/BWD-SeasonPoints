package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.services.DiscoveryService;
import dev.bwd.seasonpoints.services.LocationService;
import dev.bwd.seasonpoints.services.PointsService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerConnectionListener implements Listener {

  private final PlayerRepository playerRepository;
  private final PointsService pointsService;
  private final DiscoveryService discoveryService;
  private final SeasonPointsPlugin plugin;
  private final LocationService locationService;

  public PlayerConnectionListener(
    SeasonPointsPlugin plugin,
    PlayerRepository playerRepository,
    PointsService pointsService,
    DiscoveryService discoveryService,
    LocationService locationService
  ) {
    this.plugin = plugin;
    this.playerRepository = playerRepository;
    this.pointsService = pointsService;
    this.discoveryService = discoveryService;
    this.locationService = locationService;
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

    if (!player.hasPlayedBefore()) {
      Location safeLoc = locationService.getSafeRandomSpawnLocation();
      player.teleport(safeLoc);
    }
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if (event.isBedSpawn()) { // Ensure he as no bed
      return;
    }
    Location safeLoc = locationService.getSafeRandomSpawnLocation();
    event.setRespawnLocation(safeLoc);
  }
}
