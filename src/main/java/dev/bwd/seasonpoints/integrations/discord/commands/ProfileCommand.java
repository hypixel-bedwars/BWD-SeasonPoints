package dev.bwd.seasonpoints.integrations.discord.commands;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.integrations.discord.utils.DiscordEmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ProfileCommand implements DiscordCommand {

  private final SeasonPointsPlugin plugin;

  public ProfileCommand(SeasonPointsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public SlashCommandData getCommandData() {
    return Commands.slash(
      "profile",
      "Check a player's seasonal points profile status"
    ).addOption(
      OptionType.STRING,
      "username",
      "The Minecraft username to lookup",
      false
    );
  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    // Defer reply immediately since database lookups run on async tasks and take time
    event.deferReply(false).queue();

    OptionMapping userOption = event.getOption("username");
    String targetUser = (userOption != null)
      ? userOption.getAsString()
      : "Yourself";

    // This is where you would hook into your PointsService, SeasonService, etc.
    // e.g., int points = plugin.getPointsService().getPoints(targetUser);

    try {
      EmbedBuilder embed = DiscordEmbedUtils.createBaseEmbed(
        "📊 Seasonal Profile: " + targetUser
      )
        .addField("Current Season Points", "1,250 💎", true)
        .addField("Global Rank", "#14", true)
        .addField("Verification Status", "Linked ✅", false);

      event.getHook().sendMessageEmbeds(embed.build()).queue();
    } catch (Exception e) {
      event
        .getHook()
        .sendMessageEmbeds(
          DiscordEmbedUtils.createError(
            "Could not retrieve profile statistics."
          )
        )
        .queue();
      plugin
        .getLogger()
        .severe("Error handling /profile command execution: " + e.getMessage());
    }
  }
}
