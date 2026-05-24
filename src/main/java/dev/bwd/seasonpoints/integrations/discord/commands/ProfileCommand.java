package dev.bwd.seasonpoints.integrations.discord.commands;

import dev.bwd.seasonpoints.SeasonPointsPlugin;
import dev.bwd.seasonpoints.integrations.discord.helpers.ProfileHelper;
import dev.bwd.seasonpoints.integrations.discord.helpers.ProfileSnapshot;
import dev.bwd.seasonpoints.integrations.discord.utils.DiscordEmbedUtils;
import dev.bwd.seasonpoints.models.SeasonPlayer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ProfileCommand implements DiscordCommand {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(
    "MMM d, yyyy"
  );

  private final ProfileHelper profileHelper;

  public ProfileCommand(SeasonPointsPlugin plugin) {
    this.profileHelper = new ProfileHelper(plugin);
  }

  @Override
  public SlashCommandData getCommandData() {
    return Commands.slash("profile", "View a player's season points profile")
      .addOption(
        OptionType.USER,
        "user",
        "The Discord user to look up (defaults to you)",
        false
      );
  }

  @Override
  public void execute(SlashCommandInteractionEvent event) {
    event.deferReply().queue();

    OptionMapping userOption = event.getOption("user");
    User target = userOption != null ? userOption.getAsUser() : event.getUser();

    ProfileSnapshot snapshot = profileHelper.getProfile(target.getId());

    if (snapshot == null) {
      event
        .getHook()
        .sendMessageEmbeds(
          DiscordEmbedUtils.info(
            "No linked account",
            "**" +
              target.getEffectiveName() +
              "** isn't linked to a Minecraft account yet. Join the server with the matching name to get verified."
          )
        )
        .queue();
      return;
    }

    SeasonPlayer player = snapshot.player();
    String source = snapshot.fromCache() ? "Live • Online now" : "Stored snapshot";

    EmbedBuilder embed = DiscordEmbedUtils.base()
      .setAuthor(
        player.getUsername(),
        null,
        DiscordEmbedUtils.avatarUrl(player.getUuid())
      )
      .setTitle("Season " + snapshot.seasonId() + " Profile")
      .setThumbnail(DiscordEmbedUtils.avatarUrl(player.getUuid()))
      .addField(
        "Season Points",
        "**" + formatPoints(snapshot.seasonPoints()) + "**",
        true
      )
      .addField(
        "Lifetime Points",
        "**" + formatPoints(snapshot.lifetimePoints()) + "**",
        true
      )
      .addBlankField(true)
      .addField("Member Since", formatDate(player.getFirstJoined()), true)
      .addField(
        "Last Seen",
        snapshot.fromCache() ? "Currently online" : formatDate(player.getLastSeen()),
        true
      )
      .addBlankField(true)
      .setFooter("BWD Season Points  •  " + source);

    event.getHook().sendMessageEmbeds(embed.build()).queue();
  }

  private static String formatPoints(int value) {
    return String.format("%,d", value);
  }

  private static String formatDate(LocalDateTime dateTime) {
    if (dateTime == null) return "Unknown";
    return DATE_FORMAT.format(dateTime.atOffset(ZoneOffset.UTC));
  }
}
