package dev.bwd.seasonpoints.integrations.discord.commands;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.integrations.discord.utils.DiscordEmbedUtils;
import dev.bwd.seasonpoints.models.LeaderboardEntry;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class LeaderboardCommand implements DiscordCommand {

  private static final int DEFAULT_LIMIT = 10;
  private static final int MAX_LIMIT = 25;

  private final SeasonPointsPlugin plugin;

  public LeaderboardCommand(SeasonPointsPlugin plugin) {
    this.plugin = plugin;
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

    StringBuilder body = new StringBuilder();
    for (int i = 0; i < entries.size(); i++) {
      LeaderboardEntry entry = entries.get(i);
      body
        .append("`")
        .append(rankBadge(i + 1))
        .append("`  **")
        .append(entry.username())
        .append("** — ")
        .append(String.format("%,d", entry.points()))
        .append(" pts\n");
    }

    EmbedBuilder embed = DiscordEmbedUtils.base()
      .setTitle("Season " + seasonId + " Leaderboard")
      .setDescription(body.toString().trim())
      .setFooter("BWD Season Points  •  Top " + entries.size());

    event.getHook().sendMessageEmbeds(embed.build()).queue();
  }

  private static String rankBadge(int rank) {
    return String.format("%2d", rank);
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
