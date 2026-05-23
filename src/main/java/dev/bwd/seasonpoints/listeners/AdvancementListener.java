package dev.bwd.seasonpoints.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {

  @EventHandler
  public void onAdvancement(PlayerAdvancementDoneEvent event) {
    Player player = event.getPlayer();
    Advancement advancement = event.getAdvancement();
    NamespacedKey key = advancement.getKey();

    player.sendMessage("Completed advancement: " + key);
  }
}
