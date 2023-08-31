package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.manager.BossbarManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExcludeCommand implements CommandExecutor {

    public static List<UUID> excludedPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length > 0) {
            for (String s : args) {
                if (Bukkit.getPlayerExact(s) != null) {
                    Player target = Bukkit.getPlayer(s);
                    if (excludedPlayers.contains(target.getUniqueId())) {
                        excludedPlayers.remove(target.getUniqueId());
                        commandSender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been removed from excluded players.");
                        BossbarManager.updateBossbar(target);
                    } else {
                        excludedPlayers.add(target.getUniqueId());
                        commandSender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been added to excluded players.");
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
