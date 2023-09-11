package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.ItemManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SkipItemCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("forcebattle.skip")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.skip'");
            return false;
        }
        if (ChallengeManager.getChallengeType() == null) {
            sender.sendMessage(ChatColor.RED + "No challenge selected.");
            return false;
        }
        if (args.length != 1) return false;
        if (sender instanceof Player) {
            if (Bukkit.getPlayerExact(args[0]) == null) {
                return false;
            }
            Player player = Bukkit.getPlayer(args[0]);
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                if (ItemManager.nextItem(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already collected every item!");
                    return false;
                }

                List<String> excludedItems = ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_items");

                excludedItems.add(ItemManager.itemMap.get(player.getUniqueId()).name());
                ForceBattlePlugin.getInstance().getConfig().set("excluded_items", excludedItems);
                ForceBattlePlugin.getInstance().saveConfig();

                player.sendMessage(ChatColor.GREEN + "An operator skipped your item.");

                ItemManager.markedAsFinished(player, ItemManager.itemMap.get(player.getUniqueId()));
                ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                if (ItemManager.nextItem(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already finished every block!");
                    return false;
                }

                List<String> excludedBlocks = ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_blocks");

                excludedBlocks.add(ItemManager.blockMap.get(player.getUniqueId()).name());
                ForceBattlePlugin.getInstance().getConfig().set("excluded_blocks", excludedBlocks);
                ForceBattlePlugin.getInstance().saveConfig();

                player.sendMessage(ChatColor.GREEN + "An operator skipped your block.");

                ItemManager.markedAsFinished(player, ItemManager.blockMap.get(player.getUniqueId()));
                ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                if (ItemManager.nextMob(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already killed every mob!");
                    return false;
                }

                List<String> excludedMobs = ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_mobs");

                excludedMobs.add(ItemManager.entityMap.get(player.getUniqueId()).name());
                ForceBattlePlugin.getInstance().getConfig().set("excluded_mobs", excludedMobs);
                ForceBattlePlugin.getInstance().saveConfig();

                player.sendMessage(ChatColor.GREEN + "An operator skipped your mob.");

                ItemManager.markedAsFinished(player, ItemManager.entityMap.get(player.getUniqueId()));
                ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                if (ItemManager.nextBiome(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already found every biome!");
                    return false;
                }

                List<String> excludedBiomes = ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_biomes");

                excludedBiomes.add(ItemManager.biomeMap.get(player.getUniqueId()).name());
                ForceBattlePlugin.getInstance().getConfig().set("excluded_biomes", excludedBiomes);
                ForceBattlePlugin.getInstance().saveConfig();

                player.sendMessage(ChatColor.GREEN + "An operator skipped your biome.");

                ItemManager.markedAsFinished(player, ItemManager.entityMap.get(player.getUniqueId()));
                ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                if (ItemManager.nextAdvancement(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already finished every advancement!");
                    return false;
                }

                List<String> excludedAdvancements = ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_advancements");

                excludedAdvancements.add(ItemManager.advancementMap.get(player.getUniqueId()).name());
                ForceBattlePlugin.getInstance().getConfig().set("excluded_advancements", excludedAdvancements);
                ForceBattlePlugin.getInstance().saveConfig();

                player.sendMessage(ChatColor.GREEN + "An operator skipped your advancement.");

                ItemManager.markedAsFinished(player, ItemManager.advancementMap.get(player.getUniqueId()));
                ItemManager.advancementMap.put(player.getUniqueId(), ItemManager.nextAdvancement(player));
            }
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
        }
        return false;
    }
}