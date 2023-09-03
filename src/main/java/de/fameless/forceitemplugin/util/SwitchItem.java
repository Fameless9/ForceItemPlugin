package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.ItemManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SwitchItem implements Listener {

    private static final ItemStack switchItem = ItemProvider.ItemBuilder(new ItemStack(Material.STRUCTURE_VOID, 3), ItemProvider.enchantments(), 0, Collections.emptyList(),
            ChatColor.BLUE + "Switcher", ChatColor.BLUE + "Rightclick to switch your item/block/mob");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != (Action.RIGHT_CLICK_BLOCK) && event.getAction() != (Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() != null && event.getItem().getItemMeta().equals(switchItem.getItemMeta())) {
            event.setCancelled(true);

            if (ChallengeManager.getChallengeType() == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't skip item, as no challenge has been selected");
                return;
            }
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM) || ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                if (ItemManager.nextItem(event.getPlayer()) == null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as you have finished the challenge already!");
                    return;
                }
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                if (ItemManager.nextMob(event.getPlayer()) == null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as you have finished the challenge already!");
                    return;
                }
            }
            if (!Timer.isRunning()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as the challenge hasn't been started.");
                return;
            }

            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                List<Player> availablePlayers = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (ItemManager.blockMap.get(player.getUniqueId()) != null) {
                        availablePlayers.add(player);
                    }
                }
                if (availablePlayers.size() < 2) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }

                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);
                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player player = availablePlayers.get(random.nextInt(availablePlayers.size()));

                Material playerItem = ItemManager.blockMap.get(player.getUniqueId());
                Material otherItem = ItemManager.blockMap.get(event.getPlayer().getUniqueId());

                ItemManager.blockMap.put(player.getUniqueId(), otherItem);
                ItemManager.blockMap.put(event.getPlayer().getUniqueId(), playerItem);

                player.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " switched their block with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your block has was switched with " + player.getName() + "'s.");

                NametagManager.updateNametag(player);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(player);
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                List<Player> availablePlayers = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (ItemManager.itemMap.get(player.getUniqueId()) != null) {
                        availablePlayers.add(player);
                    }
                }
                if (availablePlayers.size() < 2) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }

                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);
                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player player = availablePlayers.get(random.nextInt(availablePlayers.size()));

                Material playerItem = ItemManager.itemMap.get(player.getUniqueId());
                Material otherItem = ItemManager.itemMap.get(event.getPlayer().getUniqueId());

                ItemManager.itemMap.put(player.getUniqueId(), otherItem);
                ItemManager.itemMap.put(event.getPlayer().getUniqueId(), playerItem);

                player.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " switched their item with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your item has was switched with " + player.getName() + "'s.");

                NametagManager.updateNametag(player);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(player);
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                List<Player> availablePlayers = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (ItemManager.entityMap.get(player.getUniqueId()) != null) {
                        availablePlayers.add(player);
                    }
                }
                if (availablePlayers.size() < 2) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }

                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);
                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player player = availablePlayers.get(random.nextInt(availablePlayers.size()));

                EntityType playerMob = ItemManager.entityMap.get(player.getUniqueId());
                EntityType otherMob = ItemManager.entityMap.get(event.getPlayer().getUniqueId());

                ItemManager.entityMap.put(player.getUniqueId(), otherMob);
                ItemManager.entityMap.put(event.getPlayer().getUniqueId(), playerMob);

                player.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " switched their mob with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your mob has was switched with " + player.getName() + "'s.");

                NametagManager.updateNametag(player);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(player);
                BossbarManager.updateBossbar(event.getPlayer());
            }
        }
    }

    public static ItemStack getSwitchItem() {
        return switchItem;
    }
}