package dev.bwd.seasonpoints.integrations.discord.commands;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.integrations.discord.helpers.ProfileHelper;
import dev.bwd.seasonpoints.integrations.discord.utils.DiscordEmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ProfileCommand implements DiscordCommand {

  private final ProfileHelper profileHelper;

  public ProfileCommand(SeasonPointsPlugin plugin) {
    this.profileHelper = new ProfileHelper(plugin);
  }

  @Override
  public SlashCommandData getCommandData() {
    return Commands.slash(
      "profile",
      "Check a player's seasonal points profile status"
    ).addOption(
      OptionType.USER,
      "username",
      "The Minecraft username or Discord user to check (optional)",
      false
    );
  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    event.deferReply(false).queue();

    OptionMapping userOption = event.getOption("username");

    String targetUser;
    String discordId;

    if (userOption != null) {
      targetUser = userOption.getAsUser().getName();
      discordId = userOption.getAsUser().getId();
    } else {
      targetUser = event.getUser().getName();
      discordId = event.getUser().getId();
    }

    int points = profileHelper.getSeasonPoints(discordId);

    EmbedBuilder embed = DiscordEmbedUtils.createBaseEmbed(
      "📊 Seasonal Profile: " + targetUser
    ).addField("Season Points", String.valueOf(points), true);

    event.getHook().sendMessageEmbeds(embed.build()).queue();
  }
}
