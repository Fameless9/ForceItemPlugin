package de.fameless.forceitemplugin.team;

import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import de.fameless.forceitemplugin.manager.PointsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamInviteReactCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /invite <accept/decline>.");
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (TeamCommand.inviteMap.containsKey(player.getUniqueId())) {
                Team team = TeamCommand.inviteMap.get(player.getUniqueId());
                String s2 = args[0];
                switch (s2) {
                    case "accept": {
                        team.addPlayer(player);
                        PointsManager.setPoints(player, team.getPoints());
                        NametagManager.updateNametag(player);
                        BossbarManager.updateBossbar(player);
                        for (UUID playerId : team.getPlayers()) {
                            if (Bukkit.getPlayer(playerId) != null) {
                                Player player2 = Bukkit.getPlayer(playerId);
                                if (player2 == player) {
                                    continue;
                                }
                                player2.sendMessage(ChatColor.GREEN + player.getName() + " has joined your team.");
                            }
                        }
                        break;
                    }
                    case "decline": {
                        TeamCommand.inviteMap.remove(player.getUniqueId());
                        for (UUID playerId : team.getPlayers()) {
                            if (Bukkit.getPlayer(playerId) != null) {
                                Player player2 = Bukkit.getPlayer(playerId);
                                player.sendMessage(ChatColor.GREEN + "You have declined the invite.");
                                player2.sendMessage(ChatColor.GREEN + player.getName() + " has declined your invite.");
                            }
                        }
                        break;
                    }
                    default: {
                        sender.sendMessage(ChatColor.RED + "Usage: /invite <accept/decline>.");
                        break;
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "You haven't been invited to a team.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
        }
        return false;
    }
}