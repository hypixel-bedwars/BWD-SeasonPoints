package dev.bwd.seasonpoints.integrations.discord;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import java.util.UUID;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class DiscordVerificationClient {

  private final SeasonPointsPlugin plugin;

  private final DiscordService discordService;

  private String normalizeNickname(String nickname) {
    if (nickname == null) {
      return "";
    }

    return nickname.replaceAll("\\[[^\\]]+\\]\\s*", "").trim().toLowerCase();
  }

  public DiscordVerificationClient(
    SeasonPointsPlugin plugin,
    DiscordService discordService
  ) {
    this.plugin = plugin;

    this.discordService = discordService;
  }

  public boolean isVerified(UUID uuid, String minecraftUsername) {
    String guildId = plugin.getConfig().getString("discord.guild-id");
    String verifiedRoleId = plugin
      .getConfig()
      .getString("discord.verified-role-id");

    if (guildId == null || verifiedRoleId == null) {
      plugin
        .getLogger()
        .warning("Guild ID or Verified Role ID not configured!");
      return false;
    }

    Guild guild = discordService.getJda().getGuildById(guildId);
    if (guild == null) {
      plugin.getLogger().warning("Guild not found: " + guildId);
      return false;
    }

    Role verifiedRole = guild.getRoleById(verifiedRoleId);
    if (verifiedRole == null) {
      plugin.getLogger().warning("Verified role not found: " + verifiedRoleId);
      return false;
    }

    for (Member member : guild.getMembers()) {
      String effectiveName = member.getEffectiveName();
      String normalized = normalizeNickname(effectiveName);

      if (normalized.equalsIgnoreCase(minecraftUsername)) {
        plugin
          .getLogger()
          .info(
            "Found match for " +
              minecraftUsername +
              " (Discord: " +
              member.getUser().getName() +
              ")"
          );

        if (member.getRoles().contains(verifiedRole)) {
          plugin
            .getLogger()
            .info("Verification successful for: " + minecraftUsername);
          return true;
        } else {
          plugin
            .getLogger()
            .warning(
              "User " +
                minecraftUsername +
                " found, but they lack the verified role."
            );
        }
      }
    }

    plugin
      .getLogger()
      .info("No verified match found for: " + minecraftUsername);
    return false;
  }

  public String getDiscordId(String minecraftUsername) {
    String guildId = plugin.getConfig().getString("discord.guild-id");
    if (guildId == null) return null;

    Guild guild = discordService.getJda().getGuildById(guildId);
    if (guild == null) {
      plugin.getLogger().warning("Cannot fetch Discord ID: Guild not found.");
      return null;
    }

    plugin
      .getLogger()
      .info(
        "Searching cache for Discord ID for: " +
          minecraftUsername +
          " (Cache size: " +
          guild.getMembers().size() +
          ")"
      );

    for (Member member : guild.getMembers()) {
      String effectiveName = member.getEffectiveName();
      String normalized = normalizeNickname(effectiveName);

      if (normalized.equalsIgnoreCase(minecraftUsername)) {
        plugin
          .getLogger()
          .info(
            "Match found! Discord ID for " +
              minecraftUsername +
              " is " +
              member.getId()
          );
        return member.getId();
      }
    }

    plugin
      .getLogger()
      .info("No Discord member matched the name: " + minecraftUsername);
    return null;
  }
}
