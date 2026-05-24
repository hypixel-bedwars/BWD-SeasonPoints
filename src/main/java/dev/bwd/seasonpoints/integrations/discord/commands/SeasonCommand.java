package dev.bwd.seasonpoints.integrations.discord.commands;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.integrations.discord.utils.DiscordEmbedUtils;
import dev.bwd.seasonpoints.models.Season;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SeasonCommand implements DiscordCommand {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(
    "MMM d, yyyy"
  );

  private final SeasonPointsPlugin plugin;

  public SeasonCommand(SeasonPointsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public SlashCommandData getCommandData() {
    return Commands.slash("season", "Show details about the current season");
  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    event.deferReply().queue();

    int seasonId = plugin.getSeasonService().getCurrentSeasonId();
    Season season = plugin.getSeasonRepository().getSeason(seasonId);

    if (season == null) {
      event
        .getHook()
        .sendMessageEmbeds(
          DiscordEmbedUtils.error(
            "Season " +
              seasonId +
              " has no database entry yet. Try again after the plugin finishes initializing."
          )
        )
        .queue();
      return;
    }

    int activePlayers = plugin
      .getPointsRepository()
      .getActivePlayerCount(seasonId);
    long totalPoints = plugin
      .getPointsRepository()
      .getTotalPointsAwarded(seasonId);

    long daysIn = Duration.between(season.startedAt(), LocalDateTime.now())
      .toDays();

    EmbedBuilder embed = DiscordEmbedUtils.base()
      .setTitle(season.name())
      .setDescription(
        season.isActive()
          ? "The current season is live — every point you earn counts."
          : "This season has concluded."
      )
      .addField("Season", "#" + season.id(), true)
      .addField("Status", season.isActive() ? "Active" : "Concluded", true)
      .addField("Day", String.valueOf(Math.max(0, daysIn) + 1), true)
      .addField("Started", formatDate(season.startedAt()), true)
      .addField(
        "Ended",
        season.endedAt() == null ? "—" : formatDate(season.endedAt()),
        true
      )
      .addBlankField(true)
      .addField("Players Scoring", String.format("%,d", activePlayers), true)
      .addField("Total Points", String.format("%,d", totalPoints), true)
      .addBlankField(true);

    event.getHook().sendMessageEmbeds(embed.build()).queue();
  }

  private static String formatDate(LocalDateTime dateTime) {
    if (dateTime == null) return "Unknown";
    return DATE_FORMAT.format(dateTime.atOffset(ZoneOffset.UTC));
  }
}
