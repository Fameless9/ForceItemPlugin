package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.ForceItemPlugin;
import de.fameless.forceitemplugin.util.ChallengeType;
import de.fameless.forceitemplugin.util.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChallengeManager {

    private static ChallengeType challengeType;

    public static ChallengeType getChallengeType() {
        return challengeType;
    }

    public static void setChallengeType(ChallengeType type) {
        challengeType = type;
        if (challengeType.equals(ChallengeType.FORCE_ITEM)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Item has been selected! Progress reset");
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemManager.resetProgress(player);
                ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
            Timer.setTime(ForceItemPlugin.getInstance().getConfig().getInt("challenge_duration"));
        } else if (challengeType.equals(ChallengeType.FORCE_BLOCK)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Block has been selected! Progress reset");
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemManager.resetProgress(player);
                ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
            Timer.setTime(ForceItemPlugin.getInstance().getConfig().getInt("challenge_duration"));
        }
    }
}
