package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BossbarManager {

    public static HashMap<UUID, BossBar> bossBarHashMap;

    static {
        bossBarHashMap = new HashMap<>();
    }

    public static void createBossbar(Player player) {
        if (BossbarManager.bossBarHashMap.get(player.getUniqueId()) != null) {
            BossbarManager.bossBarHashMap.get(player.getUniqueId()).addPlayer(player);
            return;
        }
        getBossbar(player);
    }

    public static void updateBossbar(Player player) {
        BossbarManager.bossBarHashMap.get(player.getUniqueId()).removePlayer(player);
        getBossbar(player);
    }

    public static void removeBossbar(Player player) {
        BossbarManager.bossBarHashMap.get(player.getUniqueId()).removePlayer(player);
    }

    private static BossBar getBossbar(Player player) {
        BossBar bossBar;
        if (ChallengeManager.getChallengeType() == null) {
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "No challenge selected. /menu", BarColor.WHITE, BarStyle.SOLID);
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            if (ItemManager.nextItem(player) == null) {
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won by collecting every item.");
                Timer.setRunning(false);
                LeaderboardManager.displayLeaderboard();
                bossBar = Bukkit.createBossBar(ChatColor.GREEN.toString() + ChatColor.BOLD + "COMPLETE" + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.RED, BarStyle.SOLID);
            } else {
                String itemName = formatItemName(ItemManager.itemMap.get(player.getUniqueId()).name().replace("_", " "));
                bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Item" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + itemName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.RED, BarStyle.SOLID);
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
            if (ItemManager.nextItem(player) == null) {
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won by completing every block.");
                Timer.setRunning(false);
                LeaderboardManager.displayLeaderboard();
                bossBar = Bukkit.createBossBar(ChatColor.GREEN.toString() + ChatColor.BOLD + "COMPLETE" + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.RED, BarStyle.SOLID);
            } else {
                String blockName = formatItemName(ItemManager.blockMap.get(player.getUniqueId()).name().replace("_", " "));
                bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Block" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + blockName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
            if (ItemManager.nextMob(player) == null) {
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won by killing every entity.");
                Timer.setRunning(false);
                LeaderboardManager.displayLeaderboard();
                bossBar = Bukkit.createBossBar(ChatColor.GREEN.toString() + ChatColor.BOLD + "COMPLETE" + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.RED, BarStyle.SOLID);
            } else {
                String entityName = formatItemName(ItemManager.entityMap.get(player.getUniqueId()).name().replace("_", " "));
                bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Entity" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entityName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
            if (ItemManager.nextBiome(player) == null) {
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won by finding every biome.");
                Timer.setRunning(false);
                LeaderboardManager.displayLeaderboard();
                bossBar = Bukkit.createBossBar(ChatColor.GREEN.toString() + ChatColor.BOLD + "COMPLETE" + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.RED, BarStyle.SOLID);
            } else {
                String entityName = formatItemName(ItemManager.biomeMap.get(player.getUniqueId()).name().replace("_", " "));
                bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Biome" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entityName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
            if (ItemManager.nextAdvancement(player) == null) {
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won by finishing every advancement.");
                Timer.setRunning(false);
                LeaderboardManager.displayLeaderboard();
                bossBar = Bukkit.createBossBar(ChatColor.GREEN.toString() + ChatColor.BOLD + "COMPLETE" + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.RED, BarStyle.SOLID);
            } else {
                String advancementName = ItemManager.advancementMap.get(player.getUniqueId()).name;
                bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Advancement" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + advancementName + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player), BarColor.WHITE, BarStyle.SOLID);
            }
        } else {
            bossBar = Bukkit.createBossBar(ChatColor.GOLD + "No challenge selected. /menu", BarColor.WHITE, BarStyle.SOLID);
        }
        bossBar.addPlayer(player);
        BossbarManager.bossBarHashMap.put(player.getUniqueId(), bossBar);
        return bossBar;
    }

    public static String formatItemName(String input) {
        String[] words = input.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            formatted.append(formattedWord).append(" ");
        }
        return formatted.toString().trim();
    }
}