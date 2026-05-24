package dev.bwd.seasonpoints.integrations.discord;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.integrations.discord.commands.DiscordCommand;
import dev.bwd.seasonpoints.integrations.discord.commands.HelpCommand;
import dev.bwd.seasonpoints.integrations.discord.commands.LeaderboardCommand;
import dev.bwd.seasonpoints.integrations.discord.commands.ProfileCommand;
import dev.bwd.seasonpoints.integrations.discord.commands.SeasonCommand;
import dev.bwd.seasonpoints.integrations.discord.events.SlashCommandListener;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordService {

  private final SeasonPointsPlugin plugin;
  private final Map<String, DiscordCommand> commandRegistry = new HashMap<>();
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

    registerCommands();

    try {
      this.jda = JDABuilder.createDefault(token)
        .enableIntents(
          GatewayIntent.GUILD_MEMBERS,
          GatewayIntent.GUILD_PRESENCES
        )
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .setChunkingFilter(ChunkingFilter.ALL)
        .addEventListeners(new SlashCommandListener(commandRegistry))
        .build();

      jda.awaitReady();
      plugin.getLogger().info("Discord bot connected and member cache loaded!");

      synchronizeSlashCommands();
    } catch (Exception exception) {
      plugin.getLogger().severe("Failed to connect to Discord!");
      exception.printStackTrace();
    }
  }

  private void registerCommands() {
    register(new ProfileCommand(plugin));
    register(new LeaderboardCommand(plugin));
    register(new SeasonCommand(plugin));
    register(new HelpCommand());
  }

  private void register(DiscordCommand command) {
    commandRegistry.put(command.getCommandData().getName(), command);
  }

  private void synchronizeSlashCommands() {
    String guildId = plugin.getConfig().getString("discord.guild-id");
    if (guildId == null || guildId.isBlank()) {
      plugin
        .getLogger()
        .warning(
          "Guild ID missing from config; skipping slash command syncing."
        );
      return;
    }

    Guild guild = jda.getGuildById(guildId);
    if (guild == null) {
      plugin
        .getLogger()
        .warning(
          "Could not find Discord guild matching configured ID: " + guildId
        );
      return;
    }

    guild
      .updateCommands()
      .addCommands(
        commandRegistry
          .values()
          .stream()
          .map(DiscordCommand::getCommandData)
          .collect(Collectors.toList())
      )
      .queue(
        success ->
          plugin
            .getLogger()
            .info(
              "Successfully synced " +
                commandRegistry.size() +
                " slash commands to Discord guild!"
            ),
        failure ->
          plugin
            .getLogger()
            .severe(
              "Failed to sync slash commands with Discord API: " +
                failure.getMessage()
            )
      );
  }

  public JDA getJda() {
    return jda;
  }
}
