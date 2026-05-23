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

    return nickname
      .replaceAll("\\[[^\\]]+\\]\\s*", "")
      .trim()
      .toLowerCase();
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
      return false;
    }

    Guild guild = discordService.getJda().getGuildById(guildId);

    if (guild == null) {
      return false;
    }

    Role verifiedRole = guild.getRoleById(verifiedRoleId);

    if (verifiedRole == null) {
      return false;
    }

    for (Member member : guild.getMembers()) {
      String nickname = member.getNickname();

      String normalizedNickname = normalizeNickname(nickname);

      if (normalizedNickname.equalsIgnoreCase(minecraftUsername)) {
        if (member.getRoles().contains(verifiedRole)) {
          return true;
        }
      }
    }

    return false;
  }

  public String getDiscordId(String minecraftUsername) {
    String guildId = plugin.getConfig().getString("discord.guild-id");

    if (guildId == null) {
      return null;
    }

    Guild guild = discordService.getJda().getGuildById(guildId);

    if (guild == null) {
      return null;
    }

    for (Member member : guild.getMembers()) {
      String nickname = member.getNickname();

      String normalizedNickname = normalizeNickname(nickname);

      if (normalizedNickname.equalsIgnoreCase(minecraftUsername)) {
        return member.getId();
      }
    }

    return null;
  }
}
