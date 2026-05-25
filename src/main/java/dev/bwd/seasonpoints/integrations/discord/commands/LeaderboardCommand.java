package dev.bwd.seasonpoints.integrations.discord.commands;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.integrations.discord.utils.DiscordEmbedUtils;
import dev.bwd.seasonpoints.models.LeaderboardEntry;
import dev.bwd.seasonpoints.rendering.components.LeaderboardComponent;
import java.io.IOException;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

public class LeaderboardCommand implements DiscordCommand {

  private static final int DEFAULT_LIMIT = 10;
  private static final int MAX_LIMIT = 25;

  private final SeasonPointsPlugin plugin;
  private final LeaderboardComponent leaderboardComponent;

  public LeaderboardCommand(SeasonPointsPlugin plugin, LeaderboardComponent leaderboardComponent) {
    this.plugin = plugin;
    this.leaderboardComponent = leaderboardComponent;
  }

  @Override
  public SlashCommandData getCommandData() {
    return Commands.slash(
      "leaderboard",
      "Show the top point earners for the current season"
    ).addOption(
      OptionType.INTEGER,
      "limit",
      "How many players to show (1–25, default 10)",
      false
    );
  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    event.deferReply().queue();

    OptionMapping limitOption = event.getOption("limit");
    int limit = clamp(
      limitOption != null ? limitOption.getAsInt() : DEFAULT_LIMIT,
      1,
      MAX_LIMIT
    );

    int seasonId = plugin.getSeasonService().getCurrentSeasonId();
    List<LeaderboardEntry> entries = plugin
      .getPointsRepository()
      .getTopForSeason(seasonId, limit);

    if (entries.isEmpty()) {
      event
        .getHook()
        .sendMessageEmbeds(
          DiscordEmbedUtils.info(
            "Season " + seasonId + " Leaderboard",
            "No points have been earned yet. Be the first to score."
          )
        )
        .queue();
      return;
    }

    String title = "Season " + seasonId + " Leaderboard";

    try {
      byte[] png = leaderboardComponent.renderBytes(entries, title);
      event.getHook()
        .sendFiles(FileUpload.fromData(png, "leaderboard.png"))
        .queue();
    } catch (IOException e) {
      plugin.getLogger().severe("Failed to render leaderboard image: " + e.getMessage());
      event.getHook()
        .sendMessageEmbeds(DiscordEmbedUtils.info(title, "Could not render image."))
        .queue();
    }
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
