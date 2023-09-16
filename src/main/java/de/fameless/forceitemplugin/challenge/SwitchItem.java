package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.ItemManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.util.ItemProvider;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SwitchItem implements Listener {
    private static final ItemStack switchItem = ItemProvider.buildItem(new ItemStack(Material.STRUCTURE_VOID), ItemProvider.enchantments(),
            0, Collections.emptyList(), ChatColor.BLUE + "Swapper", ChatColor.BLUE + "Rightclick to swap your item/block/mob with another player");

    public static ItemStack getSwitchItem() {
        return SwitchItem.switchItem;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() != null && event.getItem().getItemMeta().equals(SwitchItem.switchItem.getItemMeta())) {
            event.setCancelled(true);

            if (ChallengeManager.getChallengeType() == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't swap item, as no challenge has been selected.");
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
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME) && ItemManager.nextBiome(event.getPlayer()) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as you have finished the challenge already!");
                return;
            }
            if (!Timer.isRunning()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as the challenge hasn't been started.");
                return;
            }
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                List<Player> availablePlayers = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (event.getPlayer().equals(player)) continue;
                    if (ItemManager.blockMap.get(player.getUniqueId()) == null) continue;
                    availablePlayers.add(player);
                }

                if (availablePlayers.isEmpty()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }

                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);

                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));

                Material playerItem = ItemManager.blockMap.get(target.getUniqueId());
                Material otherItem = ItemManager.blockMap.get(event.getPlayer().getUniqueId());

                ItemManager.blockMap.put(target.getUniqueId(), otherItem);
                ItemManager.blockMap.put(event.getPlayer().getUniqueId(), playerItem);

                target.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their block with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your block was swapped with " + target.getName() + "'s.");

                for (Player player3 : Bukkit.getOnlinePlayers()) {
                    if (player3 != target && player3 != event.getPlayer()) {
                        player3.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their item with " + target.getName() + ".");
                    }
                }

                target.playSound(target, Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1, 20);

                NametagManager.updateNametag(target);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(target);
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                List<Player> availablePlayers = new ArrayList<>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (event.getPlayer().equals(player)) continue;
                    if (ItemManager.itemMap.get(player.getUniqueId()) == null) continue;
                    availablePlayers.add(player);
                }

                if (availablePlayers.isEmpty()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }

                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);

                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));

                Material playerItem = ItemManager.itemMap.get(target.getUniqueId());
                Material otherItem = ItemManager.itemMap.get(event.getPlayer().getUniqueId());

                ItemManager.itemMap.put(target.getUniqueId(), otherItem);
                ItemManager.itemMap.put(event.getPlayer().getUniqueId(), playerItem);

                target.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their item with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your item was swapped with " + target.getName() + "'s.");

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players != target && players != event.getPlayer()) {
                        players.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their item with " + target.getName() + ".");
                    }
                }
                NametagManager.updateNametag(target);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(target);
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                List<Player> availablePlayers = new ArrayList<Player>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (event.getPlayer().equals(player)) continue;
                    if (ItemManager.entityMap.get(player.getUniqueId()) == null) continue;
                    availablePlayers.add(player);
                }

                if (availablePlayers.isEmpty()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }

                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);

                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));

                EntityType playerMob = ItemManager.entityMap.get(target.getUniqueId());
                EntityType otherMob = ItemManager.entityMap.get(event.getPlayer().getUniqueId());

                ItemManager.entityMap.put(target.getUniqueId(), otherMob);
                ItemManager.entityMap.put(event.getPlayer().getUniqueId(), playerMob);

                target.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their mob with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your mob was swapped with " + target.getName() + "'s.");

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players != target && players != event.getPlayer()) {
                        players.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their item with " + target.getName() + ".");
                    }
                }
                NametagManager.updateNametag(target);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(target);
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                List<Player> availablePlayers = new ArrayList<Player>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (event.getPlayer().equals(player)) {
                        continue;
                    }
                    if (ItemManager.biomeMap.get(player.getUniqueId()) == null) {
                        continue;
                    }
                    availablePlayers.add(player);
                }
                if (availablePlayers.isEmpty()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }
                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);
                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);
                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player player2 = availablePlayers.get(random.nextInt(availablePlayers.size()));
                Biome playerBiome = ItemManager.biomeMap.get(player2.getUniqueId());
                Biome otherBiome = ItemManager.biomeMap.get(event.getPlayer().getUniqueId());
                ItemManager.biomeMap.put(player2.getUniqueId(), otherBiome);
                ItemManager.biomeMap.put(event.getPlayer().getUniqueId(), playerBiome);
                player2.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their biome with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your biome was swapped with " + player2.getName() + "'s.");
                for (Player player3 : Bukkit.getOnlinePlayers()) {
                    if (player3 != player2 && player3 != event.getPlayer()) {
                        player3.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their biome with " + player2.getName() + ".");
                    }
                }
                NametagManager.updateNametag(player2);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(player2);
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                List<Player> availablePlayers = new ArrayList<>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (event.getPlayer().equals(player)) continue;
                    if (ItemManager.biomeMap.get(player.getUniqueId()) == null) continue;
                    availablePlayers.add(player);
                }

                if (availablePlayers.isEmpty()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No players available.");
                    return;
                }

                Inventory inventory = event.getPlayer().getInventory();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();
                ItemStack stack = inventory.getItem(slot);

                stack.setAmount(stack.getAmount() - 1);
                inventory.setItem(slot, stack);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));

                Advancement playerAdvancement = ItemManager.advancementMap.get(target.getUniqueId());
                Advancement otherAdvancement = ItemManager.advancementMap.get(event.getPlayer().getUniqueId());

                ItemManager.advancementMap.put(target.getUniqueId(), otherAdvancement);
                ItemManager.advancementMap.put(event.getPlayer().getUniqueId(), playerAdvancement);

                target.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their advancement with yours.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "Your advancement was swapped with " + target.getName() + "'s.");

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players != target && players != event.getPlayer()) {
                        players.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " swapped their advancement with " + target.getName() + ".");
                    }
                }
                NametagManager.updateNametag(target);
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(target);
                BossbarManager.updateBossbar(event.getPlayer());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().equals(SwitchItem.switchItem.getItemMeta())) {
            event.setCancelled(true);
        }
    }
}