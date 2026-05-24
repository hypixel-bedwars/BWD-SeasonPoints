package dev.bwd.seasonpoints.integrations.discord.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;
import java.time.Instant;

public class DiscordEmbedUtils {

    // Feel free to pull these colors or branding rules out into your config.yml!
    private static final Color PRIMARY_COLOR = new Color(46, 204, 113); // Emerald Green
    private static final Color ERROR_COLOR = new Color(231, 76, 60);    // Alizarin Red
    private static final String FOOTER_TEXT = "BWD Season Points";

    public static MessageEmbed createSuccess(String title, String description) {
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(PRIMARY_COLOR)
                .setTimestamp(Instant.now())
                .setFooter(FOOTER_TEXT)
                .build();
    }

    public static MessageEmbed createError(String description) {
        return new EmbedBuilder()
                .setTitle("❌ An Error Occurred")
                .setDescription(description)
                .setColor(ERROR_COLOR)
                .setTimestamp(Instant.now())
                .setFooter(FOOTER_TEXT)
                .build();
    }
    
    public static EmbedBuilder createBaseEmbed(String title) {
        return new EmbedBuilder()
                .setTitle(title)
                .setColor(PRIMARY_COLOR)
                .setTimestamp(Instant.now())
                .setFooter(FOOTER_TEXT);
    }
}