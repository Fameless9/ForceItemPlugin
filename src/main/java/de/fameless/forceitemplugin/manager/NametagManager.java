package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.challenge.ExcludeCommand;
import de.fameless.forceitemplugin.team.TeamManager;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Team;

public class NametagManager implements Listener {
    public static void setupNametag(Player player) {
        getNametag(player);
    }

    public static void updateNametag(Player player) {
        getNametag(player);
    }

    private static void getNametag(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString());
        if (team == null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getUniqueId().toString());
        }
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) {
            team.setSuffix(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + " (excluded)");
            return;
        }
        StringBuilder suffix = new StringBuilder();
        suffix.append(" " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player));
        if (ChallengeManager.getChallengeType() != null) {
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                if (ItemManager.nextItem(player) == null) {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GREEN + "COMPLETE");
                } else {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Item" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(ItemManager.itemMap.get(player.getUniqueId()).name().replace("_", " ")));
                }
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                if (ItemManager.nextItem(player) == null) {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GREEN + "COMPLETE");
                } else {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Block" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(ItemManager.blockMap.get(player.getUniqueId()).name().replace("_", " ")));
                }
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                if (ItemManager.nextMob(player) == null) {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GREEN + "COMPLETE");
                } else {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Entity" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(ItemManager.entityMap.get(player.getUniqueId()).name().replace("_", " ")));
                }
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                if (ItemManager.nextBiome(player) == null) {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GREEN + "COMPLETE");
                } else {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Biome" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(ItemManager.biomeMap.get(player.getUniqueId()).name().replace("_", " ")));
                }
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                if (ItemManager.nextAdvancement(player) == null) {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GREEN + "COMPLETE");
                } else {
                    suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Advancement" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + ItemManager.advancementMap.get(player.getUniqueId()).name);
                }
            }
        }
        if (TeamManager.getTeam(player) != null) {
            suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Team" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + TeamManager.getTeam(player).getId());
        }
        String formattedSuffix = String.valueOf(suffix).replace("_", " ");
        team.setSuffix(formattedSuffix);
    }

    public static void newTag(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString());
        if (team != null) {
            team.addEntry(player.getName());
        }
    }

    public static void removeTag(Player player) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString()).unregister();
        for (Player target : Bukkit.getOnlinePlayers()) {
            Team team = target.getScoreboard().getTeam(player.getUniqueId().toString());
            if (team != null) {
                team.removeEntry(player.getDisplayName());
            }
        }
    }

}