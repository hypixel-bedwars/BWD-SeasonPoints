package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.services.SurvivalService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;

public class SurvivalListener implements Listener {

  private final SurvivalService survivalService;

  // Inject your service here
  public SurvivalListener(SurvivalService survivalService) {
    this.survivalService = survivalService;
  }

  @EventHandler
  public void onOreMine(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Material material = event.getBlock().getType();

    // The service handles filtering out non-ores using the TRACKED_ORES set
    survivalService.processOreMine(player, material);
  }

  @EventHandler
  public void onFish(PlayerFishEvent event) {
    // Checking the state is much safer than checking if getCaught() is null.
    // CAUGHT_FISH means an item was actually pulled out of the water.
    if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
      survivalService.processFishing(event.getPlayer());
    }
  }

  @EventHandler
  public void onVillagerTrade(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }

    // Check if the inventory is a Villager trading menu
    if (event.getInventory().getType() != InventoryType.MERCHANT) {
      return;
    }

    // In a trading menu: Slot 0 = input 1, Slot 1 = input 2, Slot 2 = trade result
    // We only care if they click the result slot
    if (event.getRawSlot() != 2) {
      return;
    }

    // Ensure there is actually an item in the result slot (meaning they have the materials to trade)
    if (
      event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()
    ) {
      return;
    }

    // Trade is happening!
    survivalService.processVillagerTrade(player);
  }
}
