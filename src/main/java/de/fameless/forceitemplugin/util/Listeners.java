package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.ForceItemPlugin;
import de.fameless.forceitemplugin.manager.*;
import de.fameless.forceitemplugin.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class Listeners implements Listener {

    private static final ItemStack skipItem = ItemProvider.ItemBuilder(new ItemStack(Material.BARRIER, 3), ItemProvider.enchantments(), 0, Collections.emptyList(),
            ChatColor.RED + "Joker", ChatColor.DARK_GRAY + "--Rightclick on a block to skip your item--");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!ItemYML.getItemProgressConfig().contains(event.getPlayer().getName())) {
            try {
                ItemYML.addEntry(event.getPlayer());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        if (!ItemManager.itemMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextItem(event.getPlayer()) != null) {
            ItemManager.itemMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
        }

        BossbarManager.createBossbar(event.getPlayer());

        NametagManager.setupNametag(event.getPlayer());
        NametagManager.newTag(event.getPlayer());

        if (!Backpack.backpackMap.containsKey(event.getPlayer().getUniqueId())) {
            Backpack.backpackMap.put(event.getPlayer().getUniqueId(), Bukkit.createInventory(null, 9 * 3, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Backpack"));
        }

        LeaderboardManager.adjustPoints(event.getPlayer());

        event.getPlayer().setResourcePack("https://drive.usercontent.google.com/download?id=1K5On0YGYJlknv9p2Wgdz9qGyrChWn8fl&export=download&authuser=1&confirm=t&uuid=b67aa88a-90e7-42ad-ab70-deaa2eea4f9e&at=APZUnTVJb5KuZWw3nzyYMd434CfL:1693104909215");
        event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName() + " joined the game");

        if (!ForceItemPlugin.getInstance().getConfig().getBoolean(event.getPlayer().getName() + ".hasBarrier")) {
            event.getPlayer().getInventory().setItem(8, skipItem);
            ForceItemPlugin.getInstance().getConfig().set(event.getPlayer().getName() + ".hasBarrier", true);
            ForceItemPlugin.getInstance().saveConfig();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) || event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            event.getPlayer().kickPlayer(ChatColor.RED + "Resourcepack couldn't be loaded!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!Timer.isRunning()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) return;
        if (ItemManager.nextItem(player) == null) return;
        if (ItemManager.isFinished(player, event.getItem().getItemStack().getType())) return;
        if (!event.getItem().getItemStack().getType().equals(ItemManager.itemMap.get(event.getEntity().getUniqueId())))
            return;
        if (TeamManager.getTeam(player) != null) {
            for (UUID teamPlayers : TeamManager.getTeam(player).getPlayers()) {
                if (Bukkit.getPlayer(teamPlayers) != null) {
                    PointsManager.addPoint(Bukkit.getPlayer(teamPlayers));
                }
            }
            ItemManager.markedAsFinished(player, event.getItem().getItemStack().getType());
            ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
            return;
        }
        ItemManager.markedAsFinished(player, event.getItem().getItemStack().getType());
        ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
        PointsManager.addPoint(player);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getItem() != null && event.getItem().getItemMeta().equals(skipItem.getItemMeta())) {
            event.setCancelled(true);
            if (ItemManager.nextItem(event.getPlayer()) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as you have collected every item already!");
                return;
            }
            if (!Timer.isRunning()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as the challenge hasn't been started.");
                return;
            }

            Inventory inventory = event.getPlayer().getInventory();
            int slot = event.getPlayer().getInventory().getHeldItemSlot();
            ItemStack stack = inventory.getItem(slot);

            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);

            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), new ItemStack(ItemManager.itemMap.get(event.getPlayer().getUniqueId())));
            ItemManager.markedAsFinished(event.getPlayer(), ItemManager.itemMap.get(event.getPlayer().getUniqueId()));
            ItemManager.itemMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
            PointsManager.addPoint(event.getPlayer());
            NametagManager.updateNametag(event.getPlayer());
            BossbarManager.updateBossbar(event.getPlayer());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Skipped current item.");
            Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " has skipped their item.");
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().equals(skipItem.getItemMeta())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        NametagManager.removeTag(event.getPlayer());
        BossbarManager.removeBossbar(event.getPlayer());
        BossbarManager.bossBarHashMap.remove(event.getPlayer().getUniqueId());
        event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getName() + " left the game");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getPlayer().hasPermission("forceitem.changegm")) return;
        if (ExcludeCommand.excludedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Can't change gamemodes while excluded.");
        }
    }
}