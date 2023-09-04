package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.ForceItemPlugin;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("forceitem.skip")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'forceitem.skip'");
            return false;
        }
        if (ChallengeManager.getChallengeType() == null) {
            sender.sendMessage(ChatColor.RED + "No challenge selected.");
            return false;
        }
        if (args.length != 1) return false;
        if (sender instanceof Player) {
            if (Bukkit.getPlayerExact(args[0]) == null) return false;
            Player player = Bukkit.getPlayer(args[0]);

            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                if (ItemManager.nextItem(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already collected every item!");
                    return false;
                }
                List<String> excludedItems = ForceItemPlugin.getInstance().getConfig().getStringList("excluded_items");
                excludedItems.add(ItemManager.itemMap.get(player.getUniqueId()).name());
                ForceItemPlugin.getInstance().getConfig().set("excluded_items", excludedItems);
                ForceItemPlugin.getInstance().saveConfig();
                player.sendMessage(ChatColor.GREEN + "An operator skipped your item.");
                ItemManager.markedAsFinished(player, ItemManager.itemMap.get(player.getUniqueId()));
                ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                if (ItemManager.nextItem(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already collected every item!");
                    return false;
                }
                List<String> excludedBlocks = ForceItemPlugin.getInstance().getConfig().getStringList("excluded_blocks");
                excludedBlocks.add(ItemManager.blockMap.get(player.getUniqueId()).name());
                ForceItemPlugin.getInstance().getConfig().set("excluded_blocks", excludedBlocks);
                ForceItemPlugin.getInstance().saveConfig();
                player.sendMessage(ChatColor.GREEN + "An operator skipped your item.");
                ItemManager.markedAsFinished(player, ItemManager.blockMap.get(player.getUniqueId()));
                ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                if (ItemManager.nextMob(player) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has already killed every mob!");
                    return false;
                }
                List<String> excludedMobs = ForceItemPlugin.getInstance().getConfig().getStringList("excluded_mobs");
                excludedMobs.add(ItemManager.entityMap.get(player.getUniqueId()).name());
                ForceItemPlugin.getInstance().getConfig().set("excluded_mobs", excludedMobs);
                ForceItemPlugin.getInstance().saveConfig();
                player.sendMessage(ChatColor.GREEN + "An operator skipped your mob.");
                ItemManager.markedAsFinished(player, ItemManager.entityMap.get(player.getUniqueId()));
                ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
            }
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
        }
        return false;
    }
}