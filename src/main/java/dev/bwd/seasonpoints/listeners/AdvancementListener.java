package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.models.AdvancementTier;
import dev.bwd.seasonpoints.services.AdvancementService;
import java.util.Set;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {

  private final AdvancementService advancementService;

  private static final Set<String> LEGENDARY_ADVANCEMENTS = Set.of(
    "adventure/arbalistic",
    "adventure/two_birds_one_arrow",
    "adventure/sniper_duel",
    "nether/uneasy_alliance",
    "nether/create_beacon",
    "nether/create_full_beacon",
    "end/levitate",
    "husbandry/obtain_netherite_hoe",
    "husbandry/tactical_fishing"
  );

  private static final Set<String> GRINDY_ADVANCEMENTS = Set.of(
    "adventure/adventuring_time",
    "adventure/monsters_hunted",
    "husbandry/balanced_diet",
    "husbandry/complete_catalogue",
    "adventure/trim_with_all_exclusive_armor_patterns",
    "nether/all_effects",
    "nether/all_potions"
  );

  private static final Set<String> SECRET_ADVANCEMENTS = Set.of(
    "adventure/how_did_we_get_here",
    "adventure/very_very_frightening",
    "adventure/lighten_up",
    "nether/find_bastion",
    "nether/obtain_ancient_debris",
    "end/levitate",
    "husbandry/safely_harvest_honey",
    "story/enter_the_end"
  );

  private static final Set<String> RARE_ADVANCEMENTS = Set.of(
    "story/cure_zombie_villager",
    "nether/ride_strider",
    "nether/return_to_sender",
    "nether/fast_travel",
    "adventure/voluntary_exile",
    "adventure/hero_of_the_village",
    "adventure/spyglass_at_ghast",
    "adventure/play_jukebox_in_meadows",
    "husbandry/fishy_business",
    "husbandry/make_a_sign_glow",
    "husbandry/wax_on",
    "husbandry/wax_off"
  );

  public AdvancementListener(AdvancementService advancementService) {
    this.advancementService = advancementService;
  }

  @EventHandler
  public void onAdvancement(PlayerAdvancementDoneEvent event) {
    Player player = event.getPlayer();
    Advancement advancement = event.getAdvancement();
    NamespacedKey key = advancement.getKey();
    String path = key.getKey();

    if (path.startsWith("recipes/") && !path.equals("recipes/root")) {
      return;
    }

    AdvancementTier tier = null;
    if (LEGENDARY_ADVANCEMENTS.contains(path)) {
      tier = AdvancementTier.LEGENDARY;
    } else if (GRINDY_ADVANCEMENTS.contains(path)) {
      tier = AdvancementTier.GRINDY;
    } else if (SECRET_ADVANCEMENTS.contains(path)) {
      tier = AdvancementTier.SECRET;
    } else if (RARE_ADVANCEMENTS.contains(path)) {
      tier = AdvancementTier.RARE;
    }

    if (tier == null) {
      return;
    }

    advancementService.handleAdvancement(player.getUniqueId(), path, tier);
  }

  // Helper to make raw path keys look like clean titles (e.g., "story/shiny_gear" -> "Shiny Gear")
  @SuppressWarnings("unused")
  private String formatName(String path) {
    String clean = path.substring(path.lastIndexOf('/') + 1).replace('_', ' ');
    return clean.substring(0, 1).toUpperCase() + clean.substring(1);
  }
}
