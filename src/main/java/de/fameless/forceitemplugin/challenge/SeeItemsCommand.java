package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.files.ItemYML;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SeeItemsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (ChallengeManager.getChallengeType() == null) {
            sender.sendMessage(ChatColor.RED + "No challenge selected.");
            return false;
        }
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            sender.sendMessage(ChatColor.RED + "/result is only available for Force Item.");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/result <player>");
            return false;
        }
        if (Bukkit.getPlayerExact(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "Player couldn't be found.");
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);
            new GUI(player, target, 1);
        }
        return false;
    }

    static class GUI {
        public GUI(Player player, Player target, int page) {

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Target player is not available anymore!");
                player.closeInventory();
                return;
            }

            Inventory gui = Bukkit.createInventory(null, 54, "Results " + target.getName() + " | Page " + page);

            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {

                List<ItemStack> allItems = new ArrayList<>();

                for (ItemStack material : ItemYML.getFinishedItems(target)) {
                    allItems.add(new ItemStack(material));
                }

                ItemStack left;
                ItemMeta leftMeta;
                if (PageUtil.isPageValid(allItems, page - 1, 52)) {
                    left = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    leftMeta = left.getItemMeta();
                    leftMeta.setDisplayName(ChatColor.GREEN + "Go page left!");
                } else {
                    left = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    leftMeta = left.getItemMeta();
                    leftMeta.setDisplayName(ChatColor.RED + "Can't go left!");
                }
                leftMeta.setLocalizedName(page + "");
                left.setItemMeta(leftMeta);

                ItemStack right;
                ItemMeta rightMeta;
                if (PageUtil.isPageValid(allItems, page + 1, 52)) {
                    right = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    rightMeta = right.getItemMeta();
                    rightMeta.setDisplayName(ChatColor.GREEN + "Go page right!");
                } else {
                    right = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    rightMeta = right.getItemMeta();
                    rightMeta.setDisplayName(ChatColor.RED + "Can't go right!");
                }
                rightMeta.setLocalizedName(target.getName());
                right.setItemMeta(rightMeta);
                gui.setItem(0, left);
                gui.setItem(8, right);

                for (ItemStack is : PageUtil.getPageItems(allItems, page, 52)) {
                    gui.addItem(is);
                }
            }
            player.openInventory(gui);
        }
    }

    static class PageUtil {
        public static List<ItemStack> getPageItems(List<ItemStack> items, int page, int spaces) {
            int startIndex = (page - 1) * spaces;
            int endIndex = Math.min(startIndex + spaces, items.size());

            List<ItemStack> newItems = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                if (i >= 0 && i < items.size()) {
                    newItems.add(items.get(i));
                }
            }
            return newItems;
        }

        public static boolean isPageValid(List<ItemStack> items, int page, int spaces) {
            if (page <= 0) return false;

            int upperBound = page * spaces;
            int lowerBound = upperBound - spaces;

            return items.size() > lowerBound;
        }
    }
}