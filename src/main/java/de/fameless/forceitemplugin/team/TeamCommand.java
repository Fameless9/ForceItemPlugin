package de.fameless.forceitemplugin.team;

import de.fameless.forceitemplugin.manager.NametagManager;
import de.fameless.forceitemplugin.manager.PointsManager;
import de.fameless.forceitemplugin.util.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {

    public static HashMap<UUID, Team> inviteMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1) {
                switch (args[0]) {
                    case "create":
                        if (Timer.isRunning()) {
                            player.sendMessage(ChatColor.RED + "Can't do that, as the challenge has already started.");
                            return false;
                        }
                        List<UUID> players = new ArrayList<>();
                        players.add(player.getUniqueId());
                        if (TeamManager.getTeam(player) == null) {
                            TeamManager.createTeam(players);
                            TeamManager.getTeam(player).setPoints(PointsManager.getPoints(player));
                            player.sendMessage(ChatColor.GREEN + "New team has been created.");
                            NametagManager.updateNametag(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "You are already in a team!");
                        }
                        break;
                    case "invite":
                        if (Timer.isRunning()) {
                            player.sendMessage(ChatColor.RED + "Can't do that, as the challenge has already started.");
                            return false;
                        }
                        if (TeamManager.getTeam(player) == null) {
                            player.sendMessage(ChatColor.RED + "You are not currently in a team!");
                            return false;
                        }
                        if (Bukkit.getPlayerExact(args[1]) == null) {
                            player.sendMessage(ChatColor.RED + "Player couldn't be found!");
                            return false;
                        }
                        Player target = Bukkit.getPlayerExact(args[1]);
                        if (TeamManager.getTeam(target) != null) {
                            player.sendMessage(ChatColor.RED + "This player is already in a team.");
                            return false;
                        }
                        inviteMap.put(target.getUniqueId(), TeamManager.getTeam(player));
                        player.sendMessage(ChatColor.GREEN + "You invited " + target.getName() + " to your team.");
                        target.sendMessage(ChatColor.AQUA + "You have been invited to a team.\n" +
                                "Use /invite accept it, or /invite decline to decline it.");
                        break;
                    case "kick":
                        if (Timer.isRunning()) {
                            player.sendMessage(ChatColor.RED + "Can't do that, as the challenge has already started.");
                            return false;
                        }
                        if (TeamManager.getTeam(player) == null) {
                            player.sendMessage(ChatColor.RED + "You are not currently in a team!");
                            return false;
                        }
                        if (Bukkit.getPlayerExact(args[1]) == null) {
                            player.sendMessage(ChatColor.RED + "Player couldn't be found!");
                            return false;
                        }
                        Player target1 = Bukkit.getPlayerExact(args[1]);
                        if (!TeamManager.getTeam(player).getPlayers().contains(target1.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "This player is not part of your team.");
                            return false;
                        }
                        TeamManager.getTeam(player).removePlayer(target1);
                        player.sendMessage(ChatColor.GREEN + "Player has been kicked from your team.");
                        target1.sendMessage(ChatColor.RED + "You have been kicked from your team.");
                        NametagManager.updateNametag(target1);
                        break;
                    case "list":
                        StringBuilder builder = new StringBuilder();
                        for (Team team : TeamManager.teamMap.values()) {
                            StringBuilder builder1 = new StringBuilder();
                            for (UUID uuid : team.getPlayers()) {
                                if (Bukkit.getPlayer(uuid) != null) {
                                    Player player2 = Bukkit.getPlayer(uuid);
                                    builder1.append(player2.getName()).append(", ");
                                }
                            }
                            if (builder1.length() > 0) {
                                builder.append(ChatColor.AQUA + "ID: " + team.getId() + ": " + builder1 + "\n");
                            }
                        }
                        sender.sendMessage(ChatColor.AQUA + "Teams:\n" + builder);
                        break;
                    case "delete":
                        if (Timer.isRunning()) {
                            player.sendMessage(ChatColor.RED + "Can't do that, as the challenge has already started.");
                            return false;
                        }
                        if (TeamManager.getTeam(player) == null) {
                            player.sendMessage(ChatColor.RED + "You are not currently in a team!");
                            return false;
                        }
                        Team team = TeamManager.getTeam(player);
                        for (UUID playerId : team.getPlayers()) {
                            for (Player player1 : Bukkit.getOnlinePlayers()) {
                                if (playerId.equals(player1.getUniqueId())) {
                                    player1.sendMessage(ChatColor.RED + "Your team has been deleted.");
                                }
                            }
                        }
                        TeamManager.deleteTeam(team.getId());
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            NametagManager.updateNametag(player1);
                        }
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Usage: /team <delete/list/create/invite/kick> <player>");
                        break;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /team <delete/list/create/invite/kick> <player>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
        }
        return false;
    }
}