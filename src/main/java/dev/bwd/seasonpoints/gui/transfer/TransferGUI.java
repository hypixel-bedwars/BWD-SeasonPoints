package dev.bwd.seasonpoints.gui.transfer;

import dev.bwd.seasonpoints.gui.core.GUIButton;
import dev.bwd.seasonpoints.gui.core.GUICore;
import dev.bwd.seasonpoints.gui.utils.ItemBuilder;
import dev.bwd.seasonpoints.services.PointsService;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TransferGUI extends GUICore {

  private final Player sender;
  private final int currentSeason;
  private final UUID receiverUuid;
  private final String receiverName;
  private int amount = 0;
  private final PointsService pointsService;

  public TransferGUI(
    PointsService pointsService,
    int currentSeason,
    Player sender,
    UUID receiverUuid,
    String receiverName
  ) {
    super(27, "Transfer Points");

    this.pointsService = pointsService;
    this.currentSeason = currentSeason;
    this.sender = sender;
    this.receiverUuid = receiverUuid;
    this.receiverName = receiverName;

    initialize();
  }

  @Override
  public void initialize() {
    inventory.clear();
    setButton(3, createAmountButton(Material.GOLD_NUGGET, "§a+10 Points", 10));
    setButton(2, createAmountButton(Material.EMERALD, "§a+25 Points", 25));
    setButton(1, createAmountButton(Material.DIAMOND, "§a+50 Points", 50));
    setButton(
      0,
      createAmountButton(Material.NETHER_STAR, "§a+100 Points", 100)
    );

    inventory.setItem(
      13,
      new ItemBuilder(Material.SUNFLOWER)
        .setName("§6§lTransfer Amount")
        .setLore(
          List.of(
            "§7Receiver:",
            "§f" + receiverName,
            "",
            "§7Current Amount:",
            "§e" + amount + " points"
          )
        )
        .setGlowing(true)
        .build()
    );

    setButton(5, createSubtractButton(Material.REDSTONE, "§c-10 Points", 10));
    setButton(6, createSubtractButton(Material.RED_DYE, "§c-25 Points", 25));
    setButton(
      7,
      createSubtractButton(Material.REDSTONE_BLOCK, "§c-50 Points", 50)
    );
    setButton(8, createSubtractButton(Material.TNT, "§c-100 Points", 100));
    setButton(
      20,
      new GUIButton(
        new ItemBuilder(Material.EMERALD_BLOCK)

          .setName("§a§lConfirm Transfer")

          .setLore(
            List.of(
              "§7Send:",
              "§e" + amount + " points",
              "",
              "§7To:",
              "§f" + receiverName
            )
          )
          .build()
      ) {
        @Override
        public void onClick(InventoryClickEvent event) {
          sender.sendMessage(
            "§aTransferred " + amount + " points to " + receiverName
          );
          sender.closeInventory();

          boolean success = pointsService.transferPointsAsync(
            currentSeason,
            sender.getUniqueId(),
            receiverUuid,
            amount
          );

          if (success) {
            sender.sendMessage("§aTransfer successful!");
            sender.closeInventory();
          } else {
            sender.sendMessage("§cTransfer failed.");
          }
        }
      }
    );

    setButton(
      24,
      new GUIButton(
        new ItemBuilder(Material.BARRIER)

          .setName("§c§lCancel")

          .build()
      ) {
        @Override
        public void onClick(InventoryClickEvent event) {
          sender.closeInventory();
        }
      }
    );
  }

  private GUIButton createAmountButton(
    Material material,
    String name,
    int increase
  ) {
    return new GUIButton(
      new ItemBuilder(material)

        .setName(name)

        .build()
    ) {
      @Override
      public void onClick(InventoryClickEvent event) {
        amount += increase;

        initialize();
      }
    };
  }

  private GUIButton createSubtractButton(
    Material material,
    String name,
    int decrease
  ) {
    return new GUIButton(
      new ItemBuilder(material)

        .setName(name)

        .build()
    ) {
      @Override
      public void onClick(InventoryClickEvent event) {
        amount = Math.max(0, amount - decrease);

        initialize();
      }
    };
  }
}
