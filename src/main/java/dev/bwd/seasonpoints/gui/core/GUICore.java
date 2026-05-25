package dev.bwd.seasonpoints.gui.core;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUICore implements InventoryHolder {

  protected final Inventory inventory;
  protected final Map<Integer, GUIButton> buttons = new HashMap<>();

  public GUICore(int size, String title) {
    this.inventory = Bukkit.createInventory(this, size, Component.text(title));
  }

  public abstract void initialize();

  public void setButton(int slot, GUIButton button) {
    buttons.put(slot, button);

    inventory.setItem(slot, button.getItem());
  }

  public void handleClick(InventoryClickEvent event) {
    GUIButton button = buttons.get(event.getSlot());

    if (button == null) {
      return;
    }

    button.onClick(event);
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }
}
