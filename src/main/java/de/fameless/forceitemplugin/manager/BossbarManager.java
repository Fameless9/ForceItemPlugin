package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.util.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class BossbarManager {

    public static final HashMap<UUID, BossBar> bossBarHashMap = new HashMap<>();

    public static void createBossbar(Player player) {
        if (bossBarHashMap.get(player.getUniqueId()) != null) {
            bossBarHashMap.get(player.getUniqueId()).addPlayer(player);
            return;
        }
        getBossbar(player);
    }

    public static void updateBossbar(Player player) {
        bossBarHashMap.get(player.getUniqueId()).removePlayer(player);
        getBossbar(player);
    }

    public static void removeBossbar(Player player) {
        bossBarHashMap.get(player.getUniqueId()).removePlayer(player);
    }

    private static BossBar getBossbar(Player player) {
        BossBar bossBar;
        if (ItemManager.nextItem(player) == null) {
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won by collecting every item.");
            Timer.setRunning(false);
            LeaderboardManager.displayLeaderboard();
            bossBar = Bukkit.createBossBar(ChatColor.GREEN.toString() + ChatColor.BOLD + "COMPLETE" + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD +
                            "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player),
                    BarColor.RED, BarStyle.SOLID);
        } else {
            String itemName = formatItemName(ItemManager.itemMap.get(player.getUniqueId()).name()).replace("_", " ");
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Item" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD +
                            itemName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD +
                            "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player),
                    BarColor.RED, BarStyle.SOLID);
        }
        bossBar.addPlayer(player);
        bossBarHashMap.put(player.getUniqueId(), bossBar);
        return bossBar;
    }

    private static String formatItemName(String input) {
        String[] words = input.split(" ");

        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            formatted.append(formattedWord).append(" ");
        }
        return formatted.toString().trim();
    }
}