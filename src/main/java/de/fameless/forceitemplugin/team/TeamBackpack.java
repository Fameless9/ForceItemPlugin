package de.fameless.forceitemplugin.team;

import de.fameless.forceitemplugin.challenge.Backpack;
import de.fameless.forceitemplugin.challenge.ChallengeCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamBackpack implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return false;
        }
        if (!ChallengeCommand.isBackpackEnabled) {
            sender.sendMessage(ChatColor.RED + "Backpacks are disabled.");
            return false;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            switch (args[0]) {
                case "team":
                    if (TeamManager.getTeam(player) == null) {
                        player.sendMessage(ChatColor.RED + "You are not currently in a team");
                        return false;
                    }
                    player.openInventory(TeamManager.getTeam(player).getBackpackInv());
                    break;
                case "open":
                    if (Backpack.getBackpack(player) == null) {
                        player.sendMessage(ChatColor.RED + "You have no backpack. This error shouldn't occur. Try rejoining.");
                        return false;
                    }
                    player.openInventory(Backpack.getBackpack(player));
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "Usage: /bp <open/team>.");
                    break;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /bp <open/team>.");
        }
        return false;
    }
}