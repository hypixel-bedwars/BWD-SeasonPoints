package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.database.repositories.PlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionListener implements Listener {

  private final PlayerRepository playerRepository;

  public PlayerConnectionListener(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    playerRepository.createPlayerIfNotExists(
      event.getPlayer().getUniqueId(),
      event.getPlayer().getName()
    );
  }
}
