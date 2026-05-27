package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class LocationService {

    private final SeasonPointsPlugin plugin;
    private final Random random = new Random();

    public LocationService(SeasonPointsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loops until a safe random location is found, up to 20 times.
     * Falls back to a potentially unsafe one if no safe spot is found to prevent server freezes.
     */
    public Location getSafeRandomSpawnLocation() {
        int maxAttempts = 20;
        int attempts = 0;
        Location loc;

        do {
            loc = getRandomSpawnLocation();
            attempts++;
            if (isSafeLocation(loc)) {
                return loc;
            }
        } while (attempts < maxAttempts);

        plugin.getLogger().warning("Could not find a safe spawn location after 20 attempts. Spawning at last checked location.");
        return loc; 
    }

    public Location getRandomSpawnLocation() {
        String worldName = plugin.getConfig().getString("spawn.world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new IllegalStateException("Configured spawn world does not exist: " + worldName);
        }

        int centerX = plugin.getConfig().getInt("spawn.center.x");
        int centerZ = plugin.getConfig().getInt("spawn.center.z");
        int radius = plugin.getConfig().getInt("spawn.radius");

        return getRandomLocation(world, centerX, centerZ, radius);
    }

    public Location getRandomLocation(World world, int centerX, int centerZ, int radius) {
        double angle = random.nextDouble() * 2 * Math.PI;
        
        // Note: Using a uniform random distribution can cluster spawns near the center.
        // Math.sqrt(random.nextDouble()) * radius spreads them out more evenly across a circle.
        double distance = Math.sqrt(random.nextDouble()) * radius; 
        
        int x = centerX + (int) (Math.cos(angle) * distance);
        int z = centerZ + (int) (Math.sin(angle) * distance);
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x + 0.5, y + 1, z + 0.5);
    }

    public boolean isSafeLocation(Location location) {
        Material feet = location.getBlock().getType();
        Material head = location.clone().add(0, 1, 0).getBlock().getType();
        Material ground = location.clone().subtract(0, 1, 0).getBlock().getType();

        // Check if head and feet are in a passable space
        if (!feet.isAir() || !head.isAir()) {
            return false;
        }

        // Check for dangerous materials
        if (ground == Material.LAVA ||
            ground == Material.WATER ||
            ground == Material.CACTUS ||
            ground == Material.MAGMA_BLOCK ||
            ground == Material.FIRE ||
            ground == Material.SOUL_FIRE) {
            return false;
        }

        return ground.isSolid();
    }
}