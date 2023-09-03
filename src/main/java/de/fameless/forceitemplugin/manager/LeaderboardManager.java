package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.ForceItemPlugin;
import de.fameless.forceitemplugin.team.Team;
import de.fameless.forceitemplugin.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class LeaderboardManager {
    private static final Map<UUID, Integer> playerPoints = new HashMap<>();

    public static void adjustPoints(Player player) {
        UUID playerId = player.getUniqueId();
        playerPoints.put(playerId, PointsManager.getPoints(player));
    }

    public static void displayLeaderboard() {

        List<Team> excluded = new ArrayList<>();

        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(playerPoints.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        StringBuilder message = new StringBuilder(ChatColor.AQUA + "Leaderboard:\n");
        int position = 1;
        for (Map.Entry<UUID, Integer> entry : sortedEntries) {
            UUID playerId = entry.getKey();
            int points = entry.getValue();
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && TeamManager.getTeam(player) != null) {
                Team team = TeamManager.getTeam(player);
                if (excluded.contains(team)) continue;
                for (UUID teamPlayers : team.getPlayers()) {
                    if (Bukkit.getPlayer(teamPlayers) != null) {
                        Bukkit.getPlayer(teamPlayers).sendMessage(ChatColor.GOLD + "Your team placed " + position + " with " + team.getPoints() + " points.");
                    }
                }
                message.append(ChatColor.GRAY + String.valueOf(position)).append(". ").append(ChatColor.BLUE + "Team " + team.getId()).append(": ").append(ChatColor.LIGHT_PURPLE + String.valueOf(points)).append(" points\n");
                position++;
                excluded.add(team);
            } else {
                if (player != null) {
                    player.sendMessage(ChatColor.GOLD + "You placed: " + position + " with " + points + " points.");
                }

                String playerName = (player != null) ? player.getName() : "Unknown";

                message.append(ChatColor.GRAY + String.valueOf(position)).append(". ").append(ChatColor.BLUE + playerName).append(": ").append(ChatColor.LIGHT_PURPLE + String.valueOf(points)).append(" points\n");
                position++;
            }
        }
        Bukkit.getServer().broadcastMessage(message.toString());
        if (!ForceItemPlugin.getInstance().getConfig().getBoolean("leaderboard_message")) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Hello, Thanks a lot for playing my plugin.\n" +
                    "I hope you didn't encounter any bugs, but if you did,\n" +
                    "please let me know so I can release a hotfix.\n" +
                    "Feedback is always welcome. If you want to leave" +
                    "your opinion about the plugin, please do so on the Spigot Resource page:\n" +
                    "https://www.spigotmc.org/threads/1-20-x-24-7-support-force-item-battle-force-block-battle.617543/" +
                    "\nThis message will not be sent again.");
            ForceItemPlugin.getInstance().getConfig().set("leaderboard_message", true);
            ForceItemPlugin.getInstance().saveConfig();
        }
    }
}