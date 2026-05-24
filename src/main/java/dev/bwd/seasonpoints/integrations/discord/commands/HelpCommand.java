package dev.bwd.seasonpoints.integrations.discord.commands;

import dev.bwd.seasonpoints.integrations.discord.utils.DiscordEmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class HelpCommand implements DiscordCommand {

  @Override
  public SlashCommandData getCommandData() {
    return Commands.slash("help", "Show available Season Points commands");
  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    EmbedBuilder embed = DiscordEmbedUtils.base()
      .setTitle("Season Points  •  Commands")
      .setDescription(
        "Track your progress, scout the leaderboard, and stay on top of the current season."
      )
      .addField(
        "/profile  `[user]`",
        "View a player's season profile. Live points when they're online, stored snapshot otherwise.",
        false
      )
      .addField(
        "/leaderboard  `[limit]`",
        "See the top earners for the current season (1–25, default 10).",
        false
      )
      .addField(
        "/season",
        "Get an overview of the current season: status, days in, total points awarded.",
        false
      )
      .addField(
        "/help",
        "Show this command list.",
        false
      );

    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
  }
}
