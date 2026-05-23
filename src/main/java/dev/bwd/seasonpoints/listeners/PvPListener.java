package dev.bwd.seasonpoints.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;


// TODO: People can kill someone for points only once
public class PvPListener implements Listener {

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {

        Player victim = event.getEntity();

        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        if (killer.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }

        ItemStack weapon = killer.getInventory().getItemInMainHand();

        victim.sendMessage(
                "You were killed by " + killer.getName() + " using " + weapon.getType().name()
        );
    }
}