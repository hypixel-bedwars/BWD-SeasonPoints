package dev.bwd.seasonpoints.integrations.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface DiscordCommand {
    
    /**
     * Defines the structure of the slash command (name, description, options/arguments).
     */
    SlashCommandData getCommandData();

    /**
     * The code executed when a user runs this slash command on Discord.
     */
    void execute(SlashCommandInteractionEvent event);
}