package de.fameless.forceitemplugin.challenge;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SeeItemsGUIListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getInventory() == null || event.getCurrentItem() == null) return;
        if (!event.getView().getTitle().contains("Results")) return;

        Player target = Bukkit.getPlayer(event.getInventory().getItem(8).getItemMeta().getLocalizedName());
        int page = Integer.parseInt(event.getInventory().getItem(0).getItemMeta().getLocalizedName());

        if (event.getRawSlot() == 0 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new SeeItemsCommand.GUI((Player) event.getWhoClicked(), target, page - 1);
        } else if (event.getRawSlot() == 8 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new SeeItemsCommand.GUI((Player) event.getWhoClicked(), target, page + 1);
        }
        event.setCancelled(true);
    }
}