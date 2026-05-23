package dev.bwd.seasonpoints.listeners;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.services.VerificationService;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class VerificationListener implements Listener {

  private final SeasonPointsPlugin plugin;

  private final VerificationService verificationService;

  public VerificationListener(
    SeasonPointsPlugin plugin,
    VerificationService verificationService
  ) {
    this.plugin = plugin;

    this.verificationService = verificationService;
  }

  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent event) {
    plugin
      .getLogger()
      .info("VerificationListener fired for: " + event.getName());

    boolean verified = verificationService.isVerified(
      event.getUniqueId(),
      event.getName()
    );

    // Log the actual result of the check
    plugin
      .getLogger()
      .info("Verification result for " + event.getName() + ": " + verified);

    if (!verified) {
      List<String> lines = plugin
        .getMessageManager()
        .getMessagesConfig()
        .getStringList("verification.not_verified");
      event.disallow(
        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
        MiniMessage.miniMessage().deserialize(String.join("\n", lines))
      );
    }
  }
}
