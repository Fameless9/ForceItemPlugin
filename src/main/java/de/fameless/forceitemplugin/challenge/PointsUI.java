package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import de.fameless.forceitemplugin.manager.PointsManager;
import de.fameless.forceitemplugin.util.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class PointsUI implements CommandExecutor, Listener {

    private static HashMap<UUID, UUID> commandMap = new HashMap<>();

    public static Inventory getPointsUI(Player target) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Manage Points");
        inventory.setItem(0, ItemProvider.buildItem(new ItemStack(Material.GOLD_NUGGET), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Adjust Points", "", ChatColor.GOLD + target.getName() + " currently has " +
                        PointsManager.getPoints(target) + " point(s).", "", ChatColor.GOLD + "Leftclick to add 1 point", "", ChatColor.GOLD + "Rightclick to subtract 1 point"));
        return inventory;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("forcebattle.points")) {
            commandSender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.points'");
            return false;
        }

        if (commandSender instanceof Player) {
            if (args.length == 1) {
                if (Bukkit.getPlayerExact(args[0]) != null) {
                    Player target = Bukkit.getPlayer(args[0]);
                    Player player = (Player) commandSender;
                    player.openInventory(getPointsUI(target));
                    PointsUI.commandMap.put(player.getUniqueId(), target.getUniqueId());
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Player couldn't be found.");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "Usage: /points <player>.");
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command.");
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getView().getTitle().endsWith("Manage Points")) {
            return;
        }
        event.setCancelled(true);

        if (event.getSlot() == 0) {
            if (Bukkit.getPlayer(PointsUI.commandMap.get(event.getWhoClicked().getUniqueId())) != null) {
                Player target = Bukkit.getPlayer(PointsUI.commandMap.get(event.getWhoClicked().getUniqueId()));
                if (event.getClick().equals(ClickType.LEFT)) {
                    PointsManager.setPoints(target, PointsManager.getPoints(target) + 1);
                    NametagManager.updateNametag(target);
                    BossbarManager.updateBossbar(target);
                    if (event.getWhoClicked() == target) {
                        event.getWhoClicked().sendMessage(ChatColor.GOLD + "Added 1 point to you.");
                    } else {
                        target.sendMessage(ChatColor.GOLD + "An operator granted 1 point to you.");
                        event.getWhoClicked().sendMessage(ChatColor.GOLD + "Added 1 point to " + target.getName() + ".");
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player != target && player != event.getWhoClicked()) {
                            player.sendMessage(ChatColor.GOLD + "Added 1 point to " + target.getName() + ".");
                        }
                    }
                    event.getWhoClicked().openInventory(getPointsUI(target));
                } else if (event.getClick().equals(ClickType.RIGHT)) {
                    if (PointsManager.getPoints(target) - 1 < 0) {
                        event.getWhoClicked().sendMessage(ChatColor.RED + "Can't go below 0 points.");
                        return;
                    }
                    PointsManager.setPoints(target, PointsManager.getPoints(target) - 1);
                    NametagManager.updateNametag(target);
                    BossbarManager.updateBossbar(target);
                    if (event.getWhoClicked() == target) {
                        event.getWhoClicked().sendMessage(ChatColor.GOLD + "Removed 1 point from you.");
                    } else {
                        target.sendMessage(ChatColor.GOLD + "An operator removed 1 point from you.");
                        event.getWhoClicked().sendMessage(ChatColor.GOLD + "Subtracted 1 point from " + target.getName() + ".");
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player != target && player != event.getWhoClicked()) {
                            player.sendMessage(ChatColor.GOLD + "Subtracted 1 point from " + target.getName() + ".");
                        }
                    }
                    event.getWhoClicked().openInventory(getPointsUI(target));
                }
            } else {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Player is not available anymore.");
                event.getWhoClicked().closeInventory();
            }
        }
    }
}