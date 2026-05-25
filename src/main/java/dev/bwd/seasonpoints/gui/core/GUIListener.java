package dev.bwd.seasonpoints.gui.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof GUICore gui)) {
      return;
    }

    event.setCancelled(true);

    gui.handleClick(event);
  }
}
