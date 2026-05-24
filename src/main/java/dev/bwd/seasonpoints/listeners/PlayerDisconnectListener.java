package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import dev.bwd.seasonpoints.services.PointsService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {

  private final PlayerRepository playerRepository;

  private final PointsService pointsService;

  public PlayerDisconnectListener(
    PlayerRepository playerRepository,
    PointsService pointsService
  ) {
    this.playerRepository = playerRepository;
    this.pointsService = pointsService;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    playerRepository.updateLastSeen(event.getPlayer().getUniqueId());
    pointsService.unloadPlayerCache(event.getPlayer().getUniqueId());
  }
}
