package dev.bwd.seasonpoints.gui.core;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class GUIButton {

  private final ItemStack item;

  public GUIButton(ItemStack item) {
    this.item = item;
  }

  public ItemStack getItem() {
    return item;
  }

  public abstract void onClick(InventoryClickEvent event);
}
