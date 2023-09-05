package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.timer.Timer;
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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player) {
            if (Timer.isRunning()) {
                commandSender.sendMessage(ChatColor.GOLD + "Can't change challenge while timer is running.");
                return false;
            }
            ((Player) commandSender).openInventory(getInventory());
        } else {
            commandSender.sendMessage(ChatColor.RED + "Only players may use this command!");
        }
        return false;
    }

    private static String currentChallenge() {
        if (ChallengeManager.getChallengeType() == null) {
            return "No challenge selected.";
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            return "Force Item";
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
            return "Force Block";
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
            return "Force Mob";
        } else {
            return "No challenge selected.";
        }
    }
    public static boolean isKeepInventory = true;
    public static boolean isBackpackEnabled;

    public static Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Challenges");
        inventory.setItem(0, ItemProvider.ItemBuilder(new ItemStack(Material.ITEM_FRAME), Collections.emptyList(),0, Collections.emptyList(),
                ChatColor.GOLD + "Force Item", "", ChatColor.BLUE + "Click to start Force Item.", "", ChatColor.BLUE + "Current Challenge: " + currentChallenge(),
                "", ChatColor.GRAY + "Progress from current challenge will be reset."));
        inventory.setItem(1, ItemProvider.ItemBuilder(new ItemStack(Material.GRASS_BLOCK), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Force Block", "", ChatColor.BLUE + "Click to start Force Block.", "", ChatColor.BLUE + "Current Challenge: " + currentChallenge(),
                "", ChatColor.GRAY + "Progress from current challenge will be reset."));
        inventory.setItem(2, ItemProvider.ItemBuilder(new ItemStack(Material.DIAMOND_SWORD), ItemProvider.enchantments(Enchantment.KNOCKBACK), 1,
                ItemProvider.itemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS), ChatColor.GOLD + "Force Mob", "", ChatColor.BLUE +
                "Click to start Force Mob.", "", ChatColor.BLUE + "Current challenge: " + currentChallenge(), "", ChatColor.GRAY + "Progress from current challenge will be reset."));
        inventory.setItem(8, ItemProvider.ItemBuilder(new ItemStack(Material.STRUCTURE_VOID), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Keep Inventory", "", ChatColor.BLUE + "Click to toggle Keep Inventory in all worlds.", "",
                ChatColor.BLUE + "Currently set to: " + !isKeepInventory));
        inventory.setItem(7, ItemProvider.ItemBuilder(new ItemStack(Material.CHEST), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Enable Backpacks", "", ChatColor.BLUE + "Click to toggle Backpacks on or off.", "",
                ChatColor.BLUE + "Currently set to: " + isBackpackEnabled));
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().endsWith("Challenges")) return;
        event.setCancelled(true);
        if (event.getSlot() == 0) {
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_ITEM);
            }
        }
        if (event.getSlot() == 1) {
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_BLOCK);
            }
        }
        if (event.getSlot() == 2) {
            if (ChallengeManager.getChallengeType() == null || !ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_MOB);
            }
        }
        if (event.getSlot() == 8) {
            for (World world : Bukkit.getServer().getWorlds()) {
                if (world != null) {
                    world.setGameRule(GameRule.KEEP_INVENTORY, isKeepInventory);
                    event.getWhoClicked().sendMessage(ChatColor.GOLD + "KeepInventory has been set to " + isKeepInventory + " for world: " + world.getName());
                }
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "Keep Inventory has been set to " + isKeepInventory);
            isKeepInventory = !isKeepInventory;
        }
        if (event.getSlot() == 7) {
            isBackpackEnabled = !isBackpackEnabled;
            if (isBackpackEnabled) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "Backpacks have been enabled.");
            } else {
                Bukkit.broadcastMessage(ChatColor.GOLD + "Backpacks have been disabled.");
            }
        }
        event.getWhoClicked().openInventory(getInventory());
    }
}