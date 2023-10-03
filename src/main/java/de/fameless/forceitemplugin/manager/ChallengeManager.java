package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.challenge.ChallengeCommand;
import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChallengeManager {

    private static ChallengeType challengeType;

    public static ChallengeType getChallengeType() {
        return ChallengeManager.challengeType;
    }

    public static void setChallengeType(ChallengeType type) {
        ChallengeManager.challengeType = type;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().endsWith("Timer")) {
                player.closeInventory();
            }
        }
        if (ChallengeManager.challengeType.equals(ChallengeType.FORCE_ITEM)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ChallengeCommand.isResetOnChange) {
                    ItemManager.resetProgress(player);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Item has been selected! Progress reset");
                } else {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Item has been selected!");
                }
                if (!ItemManager.itemMap.containsKey(player.getUniqueId())) {
                    ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                }
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        } else if (ChallengeManager.challengeType.equals(ChallengeType.FORCE_BLOCK)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ChallengeCommand.isResetOnChange) {
                    ItemManager.resetProgress(player);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Block has been selected! Progress reset");
                } else {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Block has been selected!");
                }
                if (!ItemManager.blockMap.containsKey(player.getUniqueId())) {
                    ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                }
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        } else if (ChallengeManager.challengeType.equals(ChallengeType.FORCE_MOB)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Mob has been selected! Progress reset");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ChallengeCommand.isResetOnChange) {
                    ItemManager.resetProgress(player);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Mob has been selected! Progress reset");
                } else {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Mob has been selected!");
                }
                if (!ItemManager.entityMap.containsKey(player.getUniqueId())) {
                    ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
                }
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        } else if (ChallengeManager.challengeType.equals(ChallengeType.FORCE_BIOME)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ChallengeCommand.isResetOnChange) {
                    ItemManager.resetProgress(player);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Biome has been selected! Progress reset");
                } else {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Biome has been selected!");
                }
                if (!ItemManager.biomeMap.containsKey(player.getUniqueId())) {
                    ItemManager.biomeMap.put(player.getUniqueId(), ItemManager.nextBiome(player));
                }
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        } else if (ChallengeManager.challengeType.equals(ChallengeType.FORCE_ADVANCEMENT)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ChallengeCommand.isResetOnChange) {
                    ItemManager.resetProgress(player);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Advancement has been selected! Progress reset");
                } else {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Advancement has been selected!");
                }
                if (ItemManager.nextAdvancement(player) != null) {
                    if (!ItemManager.advancementMap.containsKey(player.getUniqueId())) {
                        ItemManager.advancementMap.put(player.getUniqueId(), ItemManager.nextAdvancement(player));
                    }
                    player.sendMessage(ChatColor.DARK_GRAY + "---------------------");
                    player.sendMessage(ChatColor.GOLD + "Advancement Description:\n" + ItemManager.advancementMap.get(player.getUniqueId()).description);
                    player.sendMessage(ChatColor.DARK_GRAY + "---------------------");
                }
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        }
        Timer.setTime(Timer.getStartTime());
    }
}