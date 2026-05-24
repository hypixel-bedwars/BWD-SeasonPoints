package dev.bwd.seasonpoints.integrations.discord.events;

import dev.bwd.seasonpoints.integrations.discord.commands.DiscordCommand;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {

  private final Map<String, DiscordCommand> commands;

  public SlashCommandListener(Map<String, DiscordCommand> commands) {
    this.commands = commands;
  }

  @Override
  public void onSlashCommandInteraction(
    @NotNull SlashCommandInteractionEvent event
  ) {
    DiscordCommand command = commands.get(event.getName());

    if (command != null) {
      // Acknowledge the event asynchronously if your tasks take a split second (e.g., Database lookups)
      // For now, we'll let individual commands handle their own replies or hook deferrals.
      command.execute(event);
    } else {
      event
        .reply(
          "This command is recognized by JDA but hasn't been implemented in the plugin yet."
        )
        .setEphemeral(true)
        .queue();
    }
  }
}
