package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.manager.ChallengeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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
        } else {
            return "No challenge selected.";
        }
    }

    public static Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Challenges");
        inventory.setItem(0, ItemProvider.ItemBuilder(new ItemStack(Material.ITEM_FRAME), Collections.emptyList(),0, Collections.emptyList(),
                ChatColor.GOLD + "Force Item", "", ChatColor.BLUE + "Click to start Force Item.", "", ChatColor.BLUE + "Current Challenge: " + currentChallenge(),
                "", ChatColor.GRAY + "Progress from current challenge will be reset."));
        inventory.setItem(1, ItemProvider.ItemBuilder(new ItemStack(Material.GRASS_BLOCK), Collections.emptyList(), 0, Collections.emptyList(),
                ChatColor.GOLD + "Force Block", "", ChatColor.BLUE + "Click to start Force Block.", "", ChatColor.BLUE + "Current Challenge: " + currentChallenge(),
                "", ChatColor.GRAY + "Progress from current challenge will be reset."));
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().endsWith("Challenges")) return;
        event.setCancelled(true);
        if (event.getSlot() == 0) {
            if (ChallengeManager.getChallengeType() == null || ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_ITEM);
            }
        }
        if (event.getSlot() == 1) {
            if (ChallengeManager.getChallengeType() == null || ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                ChallengeManager.setChallengeType(ChallengeType.FORCE_BLOCK);
            }
        }
        event.getWhoClicked().openInventory(getInventory());
    }
}
