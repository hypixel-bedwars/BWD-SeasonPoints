package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.services.PvpService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PvPListener implements Listener {

  private final PvpService pvpService;

  public PvPListener(PvpService pvpService) {
    this.pvpService = pvpService;
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player victim = event.getEntity();
    Player killer = victim.getKiller();

    // Ensure it was actually a player kill, and not self-inflicted/fall damage
    if (killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
      pvpService.processPlayerKill(killer.getUniqueId(), victim.getUniqueId(), killer.getInventory().getItemInMainHand());
    }
  }
}
