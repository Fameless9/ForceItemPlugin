package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.ItemManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.util.ChallengeType;
import de.fameless.forceitemplugin.util.ItemProvider;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ChallengeCommand implements CommandExecutor, Listener {

    public static boolean isKeepInventory;
    public static boolean isBackpackEnabled;
    public static boolean isResetOnChange = true;

    private static String currentChallenge() {
        if (ChallengeManager.getChallengeType() == null) {
            return "No challenge selected.";
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            return "Force Item";
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
            return "Force Block";
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
            return "Force Mob";
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
            return "Force Biome";
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
            return "Force Advancement";
        }
        return "No challenge selected.";
    }

    public static Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Challenges");
        inventory.setItem(0, ItemProvider.buildItem(new ItemStack(Material.ITEM_FRAME), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Force Item", "", ChatColor.BLUE + "Click to start Force Item.",
                "", ChatColor.BLUE + "Current Challenge: " + currentChallenge(), "", isResetOnChange ? ChatColor.GRAY + "Progress from current challenge will be reset." : ""));
        inventory.setItem(1, ItemProvider.buildItem(new ItemStack(Material.GRASS_BLOCK), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Force Block", "", ChatColor.BLUE + "Click to start Force Block.",
                "", ChatColor.BLUE + "Current Challenge: " + currentChallenge(), "", isResetOnChange ? ChatColor.GRAY + "Progress from current challenge will be reset." : ""));
        inventory.setItem(2, ItemProvider.buildItem(new ItemStack(Material.DIAMOND_SWORD), ItemProvider.enchantments(Enchantment.KNOCKBACK), 1,
                ItemProvider.itemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS), ChatColor.GOLD + "Force Mob",
                "", ChatColor.BLUE + "Click to start Force Mob.", "", ChatColor.BLUE + "Current challenge: " + currentChallenge(), "",
                isResetOnChange ? ChatColor.GRAY + "Progress from current challenge will be reset." : ""));
        inventory.setItem(3, ItemProvider.buildItem(new ItemStack(Material.SPRUCE_SAPLING), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Force Biome", "", ChatColor.BLUE + "Click to start Force Biome.",
                "", ChatColor.BLUE + "Current challenge: " + currentChallenge(), "", isResetOnChange ? ChatColor.GRAY + "Progress from current challenge will be reset." : ""));
        inventory.setItem(4, ItemProvider.buildItem(new ItemStack(Material.END_STONE), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Force Advancement", "", ChatColor.BLUE + "Click to start Force Advancement.", "",
                ChatColor.BLUE + "Current challenge: " + currentChallenge(), "", isResetOnChange ? ChatColor.GRAY + "Progress from current challenge will be reset." : ""));
        inventory.setItem(9, ItemProvider.buildItem(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Keep Inventory", "", ChatColor.BLUE + "Click to toggle Keep Inventory in all worlds.", "",
                ChatColor.BLUE + "Currently set to: " + !ChallengeCommand.isKeepInventory));
        inventory.setItem(10, ItemProvider.buildItem(new ItemStack(Material.CHEST), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Enable Backpacks", "", ChatColor.BLUE + "Click to toggle Backpacks on or off.", "",
                ChatColor.BLUE + "Currently set to: " + isBackpackEnabled));
        inventory.setItem(11, ItemProvider.buildItem(new ItemStack(Material.REDSTONE_BLOCK), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Reset on Change", "", ChatColor.BLUE + "Click to toggle Reset on Change", ChatColor.BLUE +
                        "Resets progress on challenge select", "", ChatColor.BLUE + "Currently set to: " + isResetOnChange));
        inventory.setItem(12, ItemProvider.buildItem(new ItemStack(Material.CHAIN), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Enable Chain Mode", "", ChatColor.BLUE + "Gives every player the same item.", "",
                ChatColor.BLUE + "Currently set to: " + ChainLogic.isChainMode()));
        return inventory;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("forcebattle.menu")) {
                commandSender.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.menu'");
                return false;
            }
            ((Player) commandSender).openInventory(getInventory());
        } else {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command!");
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().endsWith("Challenges")) {
            return;
        }

        event.setCancelled(true);

        if (event.getSlot() == 0) {
            if (Timer.isRunning()) {
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Can't change challenge while timer is running.");
                return;
            }
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_ITEM);
            }
        }

        if (event.getSlot() == 1) {
            if (Timer.isRunning()) {
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Can't change challenge while timer is running.");
                return;
            }
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_BLOCK);
            }
        }

        if (event.getSlot() == 2) {
            if (Timer.isRunning()) {
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Can't change challenge while timer is running.");
                return;
            }
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_MOB);
            }
        }

        if (event.getSlot() == 3) {
            if (Timer.isRunning()) {
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Can't change challenge while timer is running.");
                return;
            }
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_BIOME);
            }
        }

        if (event.getSlot() == 4) {
            if (Timer.isRunning()) {
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "Can't change challenge while timer is running.");
                return;
            }
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_ADVANCEMENT);
            }
        }

        if (event.getSlot() == 9) {
            for (World world : Bukkit.getServer().getWorlds()) {
                if (world != null) {
                    world.setGameRule(GameRule.KEEP_INVENTORY, ChallengeCommand.isKeepInventory);
                    event.getWhoClicked().sendMessage(ChatColor.GOLD + "Keep Inventory has been set to " + ChallengeCommand.isKeepInventory + " for world: " + world.getName());
                }
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "Keep Inventory has been set to " + ChallengeCommand.isKeepInventory);
            ChallengeCommand.isKeepInventory = !ChallengeCommand.isKeepInventory;
        }

        if (event.getSlot() == 10) {
            ChallengeCommand.isBackpackEnabled = !ChallengeCommand.isBackpackEnabled;
            if (ChallengeCommand.isBackpackEnabled) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "Backpacks have been enabled.");
            } else {
                Bukkit.broadcastMessage(ChatColor.GOLD + "Backpacks have been disabled.");
            }
        }

        if (event.getSlot() == 11) {
            isResetOnChange = !isResetOnChange;
        }

        if (event.getSlot() == 12) {
            if (ChallengeManager.getChallengeType() == null) {
                event.getWhoClicked().sendMessage(ChatColor.GOLD + "No challenge selected.");
                return;
            }
            ChainLogic.setChainMode(!ChainLogic.isChainMode());
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                    ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                    ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                    ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
                } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                    ItemManager.biomeMap.put(player.getUniqueId(), ItemManager.nextBiome(player));
                } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                    ItemManager.advancementMap.put(player.getUniqueId(), ItemManager.nextAdvancement(player));
                }
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "Chain mode has been set to: " + ChainLogic.isChainMode());
        }
        event.getWhoClicked().openInventory(getInventory());
    }
}