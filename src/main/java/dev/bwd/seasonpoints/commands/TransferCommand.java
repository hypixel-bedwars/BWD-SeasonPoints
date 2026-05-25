package dev.bwd.seasonpoints.commands;

import dev.bwd.seasonpoints.gui.transfer.TransferGUI;
import dev.bwd.seasonpoints.services.PointsService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferCommand implements CommandExecutor {

  private final PointsService pointsService;
  private final int currentSeason;

  public TransferCommand(PointsService pointsService, int currentSeason) {
    this.pointsService = pointsService;
    this.currentSeason = currentSeason;
  }

  @Override
  public boolean onCommand(
    CommandSender sender,
    Command command,
    String label,
    String[] args
  ) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Only players can use this command.");

      return true;
    }

    if (args.length != 1) {
      player.sendMessage("§cUsage: /transfer <player>");

      return true;
    }

    Player target = Bukkit.getPlayerExact(args[0]);

    if (target == null) {
      player.sendMessage("§cPlayer not found.");

      return true;
    }

    if (target.equals(player)) {
      player.sendMessage("§cYou cannot transfer points to yourself.");

      return true;
    }

    TransferGUI gui = new TransferGUI(
      pointsService,
      currentSeason,
      player,
      target.getUniqueId(),
      target.getName()
    );

    player.openInventory(gui.getInventory());
    return true;
  }
}
