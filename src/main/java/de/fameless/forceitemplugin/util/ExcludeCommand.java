package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ExcludeCommand implements CommandExecutor {

    public static List<UUID> excludedPlayers = new ArrayList<>();
    public static HashMap<UUID, GameMode> gameModeHashMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!commandSender.hasPermission("forceitem.exclude")) {
            commandSender.sendMessage(ChatColor.RED + "Lacking permission: forceitem.exclude");
            return false;
        }
        if (args.length > 0) {
            for (String s : args) {
                if (Bukkit.getPlayerExact(s) != null) {
                    Player target = Bukkit.getPlayer(s);
                    if (excludedPlayers.contains(target.getUniqueId())) {
                        excludedPlayers.remove(target.getUniqueId());
                        if (commandSender.getName().equals(target.getName())) {
                            commandSender.sendMessage(ChatColor.GREEN + "You have been removed from excluded players.");
                        } else {
                            commandSender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been removed from excluded players.");
                            target.sendMessage(ChatColor.GOLD + commandSender.getName() + " has removed you from excluded players.");
                        }
                        target.setGameMode(gameModeHashMap.get(target.getUniqueId()));
                        gameModeHashMap.remove(target.getUniqueId());
                        NametagManager.updateNametag(target);
                        BossbarManager.updateBossbar(target);
                    } else {
                        if (commandSender.getName().equals(target.getName())) {
                            commandSender.sendMessage(ChatColor.GREEN + "You have been added to excluded players.");
                        } else {
                            commandSender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been added to excluded players.");
                            target.sendMessage(ChatColor.GOLD + commandSender.getName() + " has added you to excluded players.");
                        }
                        gameModeHashMap.put(target.getUniqueId(), target.getGameMode());
                        target.setGameMode(GameMode.SPECTATOR);
                        excludedPlayers.add(target.getUniqueId());
                        NametagManager.updateNametag(target);
                        BossbarManager.removeBossbar(target);
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Couldn't find player " + s + ".");
                }
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "Usage: /exclude <player> <player> ...");
        }
        return false;
    }
}