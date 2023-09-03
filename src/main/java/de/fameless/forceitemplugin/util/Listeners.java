package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.ForceItemPlugin;
import de.fameless.forceitemplugin.manager.*;
import de.fameless.forceitemplugin.team.Team;
import de.fameless.forceitemplugin.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public class Listeners implements Listener {

    private static final ItemStack skipItem = ItemProvider.ItemBuilder(new ItemStack(Material.BARRIER, 3), ItemProvider.enchantments(), 0, Collections.emptyList(),
            ChatColor.RED + "Joker", ChatColor.BLUE + "Rightclick on a block to skip your item/block");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!ItemYML.getItemProgressConfig().contains(event.getPlayer().getName())) {
            try {
                ItemYML.addEntry(event.getPlayer());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        if (!BlockYML.getBlockProgressConfig().contains(event.getPlayer().getName())) {
            try {
                BlockYML.addEntry(event.getPlayer());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!MobYML.getMobProgressConfig().contains(event.getPlayer().getName())) {
            try {
                MobYML.addEntry(event.getPlayer());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!ItemManager.itemMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextItem(event.getPlayer()) != null) {
            ItemManager.itemMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
        }
        if (!ItemManager.blockMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextItem(event.getPlayer()) != null) {
            ItemManager.blockMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
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

        if (!ForceItemPlugin.getInstance().getConfig().getBoolean(event.getPlayer().getName() + ".hasSwitch")) {
            event.getPlayer().getInventory().setItem(7, SwitchItem.getSwitchItem());
            ForceItemPlugin.getInstance().getConfig().set(event.getPlayer().getName() + ".hasSwitch", true);
            ForceItemPlugin.getInstance().saveConfig();
        }

        if (!ForceItemPlugin.isUpdated) {
            if (event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[ForceBattle] New version is available: https://www.spigotmc.org/resources/1-20-x-24-7-support-force-item-battle-force-block-battle.112328/");
            }
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
        if (ChallengeManager.getChallengeType() == null) return;
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) return;
        if (!Timer.isRunning()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) return;
        if (ItemManager.isFinished(player, event.getItem().getItemStack().getType())) return;

        if (!event.getItem().getItemStack().getType().equals(ItemManager.itemMap.get(event.getEntity().getUniqueId()))) return;
        ItemManager.markedAsFinished(player, event.getItem().getItemStack().getType());
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " found " + BossbarManager.formatItemName(event.getItem().getItemStack().getType().name()).replace("_", " "));
        ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
        PointsManager.addPoint(player);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != (Action.RIGHT_CLICK_BLOCK) && event.getAction() != (Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() != null && event.getItem().getItemMeta().equals(skipItem.getItemMeta())) {
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

            Inventory inventory = event.getPlayer().getInventory();
            int slot = event.getPlayer().getInventory().getHeldItemSlot();
            ItemStack stack = inventory.getItem(slot);

            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(slot, stack);
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), new ItemStack(ItemManager.itemMap.get(event.getPlayer().getUniqueId())));
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.itemMap.get(event.getPlayer().getUniqueId()));
                String itemName = BossbarManager.formatItemName(ItemManager.itemMap.get(event.getPlayer().getUniqueId()).name()).replace("_", " ");
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " has skipped " + itemName);
                ItemManager.itemMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
                event.getPlayer().sendMessage(ChatColor.GREEN + "Skipped current item.");
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.blockMap.get(event.getPlayer().getUniqueId()));
                String blockName = BossbarManager.formatItemName(ItemManager.blockMap.get(event.getPlayer().getUniqueId()).name()).replace("_", " ");
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " has skipped " + blockName);
                ItemManager.blockMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.entityMap.get(event.getPlayer().getUniqueId()));
                String entityName = BossbarManager.formatItemName(ItemManager.entityMap.get(event.getPlayer().getUniqueId()).name()).replace("_", " ");
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " has skipped " + entityName);
                ItemManager.entityMap.put(event.getPlayer().getUniqueId(), ItemManager.nextMob(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
            }
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
        if (TeamManager.getTeam(event.getPlayer()) != null) {
            Team team = TeamManager.getTeam(event.getPlayer());
            team.removePlayer(event.getPlayer());
            for (UUID uuid : team.getPlayers()) {
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.GOLD + event.getPlayer().getName() + " has left the game and was removed from your team.");
            }
        }
        event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getName() + " left the game");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getPlayer().hasPermission("forceitem.changegm")) return;
        if (ExcludeCommand.excludedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Can't change gamemode while excluded.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (ChallengeManager.getChallengeType() == null) return;
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) return;
        if (!Timer.isRunning()) return;
        Player player = event.getPlayer();
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) return;

        Block block = event.getTo().getBlock();
        Block blockBelow = block.getRelative(BlockFace.DOWN);

        BlockState blockState = block.getState();
        BlockState blockBelowState = blockBelow.getState();

        if (isSameBlockType(blockState, player) || isSameBlockType(blockBelowState, player)) {
            ItemManager.markedAsFinished(player, block.getType());
            Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " finished " + BossbarManager.formatItemName(ItemManager.blockMap.get(player.getUniqueId()).name()).replace("_", " "));
            ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
            PointsManager.addPoint(player);
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
        }
    }
    private boolean isSameBlockType(BlockState blockState, Player player) {
        Material playerBlockType = ItemManager.blockMap.get(player.getUniqueId());
        return blockState.getType() == playerBlockType;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (ChallengeManager.getChallengeType() == null) return;
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) return;
        if (!Timer.isRunning()) return;
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if (!event.getEntity().isDead()) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (!(entity.getHealth() - event.getDamage() <= 0)) return;
        }
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) return;

        if (event.getEntity().getType().equals(ItemManager.entityMap.get(player.getUniqueId()))) {
            ItemManager.markedAsFinished(player, event.getEntity().getType());
            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " finished " + BossbarManager.formatItemName(ItemManager.entityMap.get(player.getUniqueId()).name()).replace("_", " "));
            ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
            PointsManager.addPoint(player);
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
        }
    }

    public static ItemStack getSkipItem() {
        return skipItem;
    }
}