package dev.bwd.seasonpoints.integrations.discord;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordService {

  private final SeasonPointsPlugin plugin;

  private JDA jda;

  public DiscordService(SeasonPointsPlugin plugin) {
    this.plugin = plugin;
  }

  public void initializeDiscordService() {
    String token = plugin.getConfig().getString("discord.bot-token");

    if (token == null || token.isBlank()) {
      plugin.getLogger().severe("Discord bot token missing!");
      return;
    }

    try {
      this.jda = JDABuilder.createDefault(token)
        .enableIntents(
          GatewayIntent.GUILD_MEMBERS,
          GatewayIntent.GUILD_PRESENCES
        )
        // Force JDA to cache all members in memory
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        // Force JDA to download the entire member list on startup
        .setChunkingFilter(ChunkingFilter.ALL)
        .build();

      jda.awaitReady();

      plugin.getLogger().info("Discord bot connected and member cache loaded!");
    } catch (Exception exception) {
      plugin.getLogger().severe("Failed to connect to Discord!");
      exception.printStackTrace();
    }
  }

  public JDA getJda() {
    return jda;
  }
}
