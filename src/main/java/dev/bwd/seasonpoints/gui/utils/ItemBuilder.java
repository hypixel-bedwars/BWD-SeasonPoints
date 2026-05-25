package dev.bwd.seasonpoints.gui.utils;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

  private final ItemStack itemStack;
  private final ItemMeta itemMeta;

  public ItemBuilder(Material material) {
    this.itemStack = new ItemStack(material);

    this.itemMeta = itemStack.getItemMeta();
  }

  public ItemBuilder setName(String name) {
    itemMeta.displayName(Component.text(name));

    return this;
  }

  public ItemBuilder setLore(List<String> lore) {
    itemMeta.lore(lore.stream().map(Component::text).toList());

    return this;
  }

  public ItemBuilder addFlags(ItemFlag... flags) {
    itemMeta.addItemFlags(flags);

    return this;
  }

  public ItemBuilder setAmount(int amount) {
    itemStack.setAmount(amount);

    return this;
  }

  public ItemBuilder setGlowing(boolean glowing) {
    if (glowing) {
      itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

      itemStack.addUnsafeEnchantment(
        org.bukkit.enchantments.Enchantment.UNBREAKING,
        1
      );
    }

    return this;
  }

  public ItemStack build() {
    itemStack.setItemMeta(itemMeta);

    return itemStack;
  }
}
