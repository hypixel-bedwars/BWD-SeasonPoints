package dev.bwd.seasonpoints.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SurvivalListener implements Listener {

  @EventHandler
  public void onOreMine(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Material material = event.getBlock().getType();

    player.sendMessage("Mined " + material.name());
  }

  @EventHandler
  public void onFish(PlayerFishEvent event) {
    if (event.getCaught() == null) {
      return;
    }

    Player player = event.getPlayer();

    player.sendMessage("Fishing reward");
  }

  @EventHandler
  public void onVillagerTrade(PlayerInteractEntityEvent event) {
    if (!(event.getRightClicked() instanceof Villager)) {
      return;
    }

    Player player = event.getPlayer();
    ItemStack item = player.getInventory().getItemInMainHand();

    if (item.getType().isAir()) {
      return;
    }

    player.sendMessage("Villager interaction");
  }
}
