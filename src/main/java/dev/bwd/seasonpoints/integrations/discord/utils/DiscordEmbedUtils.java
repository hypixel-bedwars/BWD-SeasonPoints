package dev.bwd.seasonpoints.integrations.discord.utils;

import java.awt.Color;
import java.time.Instant;
import java.util.UUID;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public final class DiscordEmbedUtils {

  public static final Color PRIMARY = new Color(0xE5B970);
  public static final Color ACCENT = new Color(0x5865F2);
  public static final Color SUCCESS = new Color(0x57F287);
  public static final Color ERROR = new Color(0xED4245);
  public static final Color MUTED = new Color(0x4F545C);

  private static final String FOOTER = "BWD Season Points";
  private static final String AVATAR_URL = "https://mc-heads.net/avatar/%s/128";

  private DiscordEmbedUtils() {}

  public static EmbedBuilder base() {
    return new EmbedBuilder()
      .setColor(PRIMARY)
      .setFooter(FOOTER)
      .setTimestamp(Instant.now());
  }

  public static EmbedBuilder base(String title) {
    return base().setTitle(title);
  }

  public static MessageEmbed error(String description) {
    return new EmbedBuilder()
      .setTitle("Something went wrong")
      .setDescription(description)
      .setColor(ERROR)
      .setFooter(FOOTER)
      .setTimestamp(Instant.now())
      .build();
  }

  public static MessageEmbed info(String title, String description) {
    return new EmbedBuilder()
      .setTitle(title)
      .setDescription(description)
      .setColor(ACCENT)
      .setFooter(FOOTER)
      .setTimestamp(Instant.now())
      .build();
  }

  public static String avatarUrl(UUID uuid) {
    return String.format(AVATAR_URL, uuid.toString());
  }
}
