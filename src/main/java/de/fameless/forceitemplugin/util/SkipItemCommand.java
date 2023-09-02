package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.ForceItemPlugin;
import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.ItemManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import de.fameless.forceitemplugin.util.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boss;
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
            if (ItemManager.nextItem(player) == null) {
                sender.sendMessage(ChatColor.RED + "Player has already collected every item!");
                return false;
            }
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                List<String> excludedItems = ForceItemPlugin.getInstance().getConfig().getStringList("excluded_items");
                excludedItems.add(ItemManager.itemMap.get(player.getUniqueId()).name());
                ForceItemPlugin.getInstance().getConfig().set("excluded_items", excludedItems);
                ForceItemPlugin.getInstance().saveConfig();
                player.sendMessage(ChatColor.GREEN + "An operator skipped your item.");
                ItemManager.markedAsFinished(player, ItemManager.blockMap.get(player.getUniqueId()));
                ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                List<String> excludedBlocks = ForceItemPlugin.getInstance().getConfig().getStringList("excluded_blocks");
                excludedBlocks.add(ItemManager.blockMap.get(player.getUniqueId()).name());
                ForceItemPlugin.getInstance().getConfig().set("excluded_blocks", excludedBlocks);
                ForceItemPlugin.getInstance().saveConfig();
                player.sendMessage(ChatColor.GREEN + "An operator skipped your item.");
                ItemManager.markedAsFinished(player, ItemManager.itemMap.get(player.getUniqueId()));
                ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            }
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
        }
        return false;
    }
}