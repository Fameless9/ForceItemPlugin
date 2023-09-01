package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.team.TeamManager;
import de.fameless.forceitemplugin.util.ExcludeCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class NametagManager {

    public static void setupNametag(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString());
        if (team == null) {
            Team newTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getUniqueId().toString());
            if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) {
                newTeam.setSuffix(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + " (excluded)");
                return;
            }
            if (TeamManager.getTeam(player) == null) {
                newTeam.setSuffix(" " + ChatColor.GOLD + "Points: " + PointsManager.getPoints(player));
                return;
            }
            newTeam.setSuffix(" " + ChatColor.GOLD + "Points: " + PointsManager.getPoints(player) + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Team: " + TeamManager.getTeam(player).getId());
            return;
        }
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) {
            team.setSuffix(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + " (excluded)");
            return;
        }
        team.setSuffix(" " + ChatColor.GOLD + "Points: " + PointsManager.getPoints(player));
    }

    public static void updateNametag(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString());
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) {
            team.setSuffix(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + " (excluded)");
            return;
        }
        if (TeamManager.getTeam(player) == null) {
            team.setSuffix(" " + ChatColor.GOLD + "Points: " + PointsManager.getPoints(player));
            return;
        }
        team.setSuffix(" " + ChatColor.GOLD + "Points: " + PointsManager.getPoints(player) + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Team: " + TeamManager.getTeam(player).getId());
    }

    public static void newTag(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString());
        if (team != null) {
            team.addEntry(player.getName());
        }
    }
    public static void removeTag(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            Team team = target.getScoreboard().getTeam(player.getUniqueId().toString());
            if (team != null) {
                team.removeEntry(player.getDisplayName());
            }
        }
    }
}