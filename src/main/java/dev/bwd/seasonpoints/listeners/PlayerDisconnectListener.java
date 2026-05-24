package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.services.DiscoveryService;
import dev.bwd.seasonpoints.services.PointsService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {

  private final PlayerRepository playerRepository;
  private final PointsService pointsService;
  private final DiscoveryService discoveryService;

  public PlayerDisconnectListener(
    PlayerRepository playerRepository,
    PointsService pointsService,
    DiscoveryService discoveryService
  ) {
    this.playerRepository = playerRepository;
    this.pointsService = pointsService;
    this.discoveryService = discoveryService;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    playerRepository.updateLastSeen(player.getUniqueId());
    pointsService.unloadPlayerCache(player.getUniqueId());
    discoveryService.unloadPlayerCache(player.getUniqueId());
  }
}
