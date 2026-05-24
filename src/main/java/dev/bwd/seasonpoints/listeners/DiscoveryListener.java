package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.services.DiscoveryService;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.util.BoundingBox;

public class DiscoveryListener implements Listener {

  private final DiscoveryService discoveryService;

  public DiscoveryListener(DiscoveryService discoveryService) {
    this.discoveryService = discoveryService;
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (event.getTo() == null) {
      return;
    }

    if (
      event.getFrom().getBlockX() == event.getTo().getBlockX() &&
      event.getFrom().getBlockY() == event.getTo().getBlockY() &&
      event.getFrom().getBlockZ() == event.getTo().getBlockZ()
    ) {
      return;
    }

    Player player = event.getPlayer();

    checkBiomeDiscovery(player, event);
    checkStructureDiscovery(player);
  }

  private void checkBiomeDiscovery(Player player, PlayerMoveEvent event) {
    Biome fromBiome = event.getFrom().getBlock().getBiome();
    Biome toBiome = event.getTo().getBlock().getBiome();

    if (fromBiome == toBiome) {
      return;
    }

    // Pass the logic off to our service
    discoveryService.handleBiomeDiscovery(player, toBiome);
  }

  private void checkStructureDiscovery(Player player) {
    Location location = player.getLocation();
    World world = player.getWorld();

    int playerX = location.getBlockX();
    int playerY = location.getBlockY();
    int playerZ = location.getBlockZ();

    int chunkX = playerX >> 4;
    int chunkZ = playerZ >> 4;

    // TODO: Save to the cache so that the player doesn't get duplicate messages for the same structure.
    // Save to the db to make sure it is new structure that he has discovered
    Set<Key> discoveredStructures = new HashSet<>();

    // A chunk is 16x16. Checking a 3x3 chunk grid centered on the player covers
    // the current chunk and its neighbors, safely covering your 20-block radius.
    for (int cx = chunkX - 1; cx <= chunkX + 1; cx++) {
      for (int cz = chunkZ - 1; cz <= chunkZ + 1; cz++) {
        for (GeneratedStructure generated : world.getStructures(cx, cz)) {
          BoundingBox box = generated.getBoundingBox();

          double distanceX = Math.max(
            0,
            Math.max(box.getMinX() - playerX, playerX - box.getMaxX())
          );
          double distanceY = Math.max(
            0,
            Math.max(box.getMinY() - playerY, playerY - box.getMaxY())
          );
          double distanceZ = Math.max(
            0,
            Math.max(box.getMinZ() - playerZ, playerZ - box.getMaxZ())
          );

          double distance = Math.sqrt(
            distanceX * distanceX +
              distanceY * distanceY +
              distanceZ * distanceZ
          );

          if (distance <= 20) {
            Key key = generated.getStructure().key();

            if (discoveredStructures.add(key)) {
              player.sendMessage("Discovered structure: " + key.asString());
            }
          }
        }
      }
    }
  }

  @Override
  public String toString() {
    return "DiscoveryListener []";
  }
}
