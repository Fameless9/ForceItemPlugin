package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import de.fameless.forceitemplugin.files.*;
import de.fameless.forceitemplugin.manager.*;
import de.fameless.forceitemplugin.team.Team;
import de.fameless.forceitemplugin.team.TeamManager;
import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.util.ChallengeType;
import de.fameless.forceitemplugin.util.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
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
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;

public class Listeners implements Listener {
    private static final ItemStack skipItem = ItemProvider.buildItem(new ItemStack(Material.BARRIER, 3),
            ItemProvider.enchantments(), 0, Collections.emptyList(), ChatColor.RED + "Joker",
            ChatColor.BLUE + "Rightclick on a block to skip your item/block");

    public static boolean hasAdvancement(Player player, String name) {
        Advancement advancement = getAdvancement(name);
        if (advancement == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        return progress.isDone();
    }

    public static Advancement getAdvancement(String name) {
        Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
        while (it.hasNext()) {
            Advancement a = it.next();
            if (a.getKey().toString().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    public static void checkForItem() {
        new BukkitRunnable() {
            public void run() {
                if (ChallengeManager.getChallengeType() == null) {
                    return;
                }
                if (!Timer.isRunning()) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                        if (ItemManager.itemMap.get(player.getUniqueId()) == null) continue;
                        if (!player.getInventory().contains(ItemManager.itemMap.get(player.getUniqueId()))) continue;
                        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) continue;

                        Material material = ItemManager.itemMap.get(player.getUniqueId());

                        ItemManager.markedAsFinished(player, material);
                        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " found " + BossbarManager.formatItemName(material.name()).replace("_", " "));
                        ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                        PointsManager.addPoint(player);
                        NametagManager.updateNametag(player);
                        BossbarManager.updateBossbar(player);
                    } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                        if (ItemManager.advancementMap.get(player.getUniqueId()) == null) continue;
                        if (!hasAdvancement(player, ItemManager.advancementMap.get(player.getUniqueId()).getKey().toString()))
                            continue;

                        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " finished " + ItemManager.advancementMap.get(player.getUniqueId()).name);
                        ItemManager.markedAsFinished(player, ItemManager.advancementMap.get(player.getUniqueId()));
                        if (ChainLogic.isChainMode()) {
                            ChainLogic.chainProgressAdvancementHashMap.get(player.getUniqueId()).remove(0);
                        }
                        ItemManager.advancementMap.put(player.getUniqueId(), ItemManager.nextAdvancement(player));
                        if (ItemManager.nextAdvancement(player) != null) {
                            player.sendMessage(ChatColor.DARK_GRAY + "---------------------");
                            player.sendMessage(ChatColor.GOLD + "Advancement Description:\n" + ItemManager.advancementMap.get(player.getUniqueId()).description);
                            player.sendMessage(ChatColor.DARK_GRAY + "---------------------");
                        }
                        PointsManager.addPoint(player);
                        NametagManager.updateNametag(player);
                        BossbarManager.updateBossbar(player);
                    }
                }
            }
        }.runTaskTimer(ForceBattlePlugin.getInstance(), 0L, 1L);
    }

    public static ItemStack getSkipItem() {
        return Listeners.skipItem;
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
        if (!event.getItem().getItemStack().getType().equals(ItemManager.itemMap.get(event.getEntity().getUniqueId())))
            return;

        ItemManager.markedAsFinished(player, event.getItem().getItemStack().getType());
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " found " + BossbarManager.formatItemName(event.getItem().getItemStack().getType().name()).replace("_", " "));
        if (ChainLogic.isChainMode()) {
            ChainLogic.chainProgressItemHashMap.get(player.getUniqueId()).remove(0);
        }
        ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
        PointsManager.addPoint(player);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().equals(Listeners.skipItem.getItemMeta())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        for (org.bukkit.scoreboard.Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            team.unregister();
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
        if (!event.getPlayer().hasPermission("forcebattle.changegm")) {
            return;
        }
        if (ExcludeCommand.excludedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Can't change gamemode while excluded.");
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
        if (!Timer.isRunning())
            return;
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        if (!event.getEntity().isDead()) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (entity.getHealth() - event.getDamage() > 0) return;
        }
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) return;

        if (event.getEntity().getType().equals(ItemManager.entityMap.get(player.getUniqueId()))) {
            ItemManager.markedAsFinished(player, event.getEntity().getType());
            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " finished " + BossbarManager.formatItemName(ItemManager.entityMap.get(player.getUniqueId()).name()).replace("_", " "));
            if (ChainLogic.isChainMode()) {
                ChainLogic.chainProgressMobHashMap.get(player.getUniqueId()).remove(0);
            }
            ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
            PointsManager.addPoint(player);
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            if (ItemYML.getRootObject() == null || !ItemYML.getRootObject().has(event.getPlayer().getName())) {
                ItemYML.addEntry(event.getPlayer());
            }
            if (BlockYML.getRootObject() == null || !BlockYML.getRootObject().has(event.getPlayer().getName())) {
                BlockYML.addEntry(event.getPlayer());
            }
            if (MobYML.getRootObject() == null || !MobYML.getRootObject().has(event.getPlayer().getName())) {
                MobYML.addEntry(event.getPlayer());
            }
            if (BiomeYML.getRootObject() == null || !BiomeYML.getRootObject().has(event.getPlayer().getName())) {
                BiomeYML.addEntry(event.getPlayer());
            }
            if (AdvancementYML.getRootObject() == null || !AdvancementYML.getRootObject().has(event.getPlayer().getName())) {
                AdvancementYML.addEntry(event.getPlayer());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!ItemManager.itemMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextItem(event.getPlayer()) != null) {
            ItemManager.itemMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
        }
        if (!ItemManager.blockMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextItem(event.getPlayer()) != null) {
            ItemManager.blockMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
        }
        if (!ItemManager.entityMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextMob(event.getPlayer()) != null) {
            ItemManager.entityMap.put(event.getPlayer().getUniqueId(), ItemManager.nextMob(event.getPlayer()));
        }
        if (!ItemManager.biomeMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextBiome(event.getPlayer()) != null) {
            ItemManager.biomeMap.put(event.getPlayer().getUniqueId(), ItemManager.nextBiome(event.getPlayer()));
        }
        if (!ItemManager.advancementMap.containsKey(event.getPlayer().getUniqueId()) && ItemManager.nextAdvancement(event.getPlayer()) != null) {
            ItemManager.advancementMap.put(event.getPlayer().getUniqueId(), ItemManager.nextAdvancement(event.getPlayer()));
        }
        if (!ItemYML.finishedItemsMap.containsKey(event.getPlayer().getUniqueId())) {
            ItemYML.finishedItemsMap.put(event.getPlayer().getUniqueId(), ItemYML.getFinishedItemsFromJson(event.getPlayer()));
        }

        if (!ChainLogic.chainProgressItemHashMap.containsKey(event.getPlayer().getUniqueId())) {
            ChainLogic.chainProgressItemHashMap.put(event.getPlayer().getUniqueId(), ChainLogic.getItemChainList());
        }
        if (!ChainLogic.chainProgressBlockHashMap.containsKey(event.getPlayer().getUniqueId())) {
            ChainLogic.chainProgressBlockHashMap.put(event.getPlayer().getUniqueId(), ChainLogic.getBlockChainList());
        }
        if (!ChainLogic.chainProgressMobHashMap.containsKey(event.getPlayer().getUniqueId())) {
            ChainLogic.chainProgressMobHashMap.put(event.getPlayer().getUniqueId(), ChainLogic.getMobChainList());
        }
        if (!ChainLogic.chainProgressBiomeHashMap.containsKey(event.getPlayer().getUniqueId())) {
            ChainLogic.chainProgressBiomeHashMap.put(event.getPlayer().getUniqueId(), ChainLogic.getBiomeChainList());
        }
        if (!ChainLogic.chainProgressAdvancementHashMap.containsKey(event.getPlayer().getUniqueId())) {
            ChainLogic.chainProgressAdvancementHashMap.put(event.getPlayer().getUniqueId(), ChainLogic.getAdvancementChainList());
        }

        BossbarManager.createBossbar(event.getPlayer());
        NametagManager.setupNametag(event.getPlayer());
        NametagManager.newTag(event.getPlayer());

        if (!Backpack.backpackMap.containsKey(event.getPlayer().getUniqueId())) {
            Backpack.backpackMap.put(event.getPlayer().getUniqueId(), Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Backpack"));
        }

        LeaderboardManager.adjustPoints(event.getPlayer());
        event.getPlayer().setResourcePack("https://drive.usercontent.google.com/download?id=1K5On0YGYJlknv9p2Wgdz9qGyrChWn8fl&export=download&authuser=1&confirm=t&uuid=b67aa88a-90e7-42ad-ab70-deaa2eea4f9e&at=APZUnTVJb5KuZWw3nzyYMd434CfL:1693104909215");
        event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName() + " joined the game");

        if (!ForceBattlePlugin.getInstance().getConfig().getBoolean(event.getPlayer().getName() + ".hasBarrier")) {
            event.getPlayer().getInventory().setItem(8, Listeners.skipItem);
            ForceBattlePlugin.getInstance().getConfig().set(event.getPlayer().getName() + ".hasBarrier", true);
            ForceBattlePlugin.getInstance().saveConfig();
        }

        if (!ForceBattlePlugin.getInstance().getConfig().getBoolean(event.getPlayer().getName() + ".hasSwitch")) {
            event.getPlayer().getInventory().setItem(7, SwitchItem.getSwitchItem());
            ForceBattlePlugin.getInstance().getConfig().set(event.getPlayer().getName() + ".hasSwitch", true);
            ForceBattlePlugin.getInstance().saveConfig();
        }

        if (!ForceBattlePlugin.isUpdated && event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(ChatColor.GRAY + "[ForceBattle] New version is available: https://www.spigotmc.org/resources/1-20-x-24-7-support-force-item-battle-force-block-battle.112328/");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() != null && event.getItem().getItemMeta().equals(Listeners.skipItem.getItemMeta())) {
            event.setCancelled(true);
            if (ChallengeManager.getChallengeType() == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't skip item, as no challenge has been selected.");
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
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                if (ItemManager.nextBiome(event.getPlayer()) == null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as you have finished the challenge already!");
                    return;
                }
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT) && ItemManager.nextAdvancement(event.getPlayer()) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't do that, as you have finished the challenge already!");
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

            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), new ItemStack(ItemManager.itemMap.get(event.getPlayer().getUniqueId())));
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.itemMap.get(event.getPlayer().getUniqueId()));
                String itemName = BossbarManager.formatItemName(ItemManager.itemMap.get(event.getPlayer().getUniqueId()).name()).replace("_", " ");
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " skipped " + itemName);
                if (ChainLogic.isChainMode()) {
                    ChainLogic.chainProgressItemHashMap.get(event.getPlayer().getUniqueId()).remove(0);
                }
                ItemManager.itemMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
                event.getPlayer().sendMessage(ChatColor.GREEN + "Skipped current item.");
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.blockMap.get(event.getPlayer().getUniqueId()));
                String blockName = BossbarManager.formatItemName(ItemManager.blockMap.get(event.getPlayer().getUniqueId()).name()).replace("_", " ");
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " skipped " + blockName);
                if (ChainLogic.isChainMode()) {
                    ChainLogic.chainProgressBlockHashMap.get(event.getPlayer().getUniqueId()).remove(0);
                }
                ItemManager.blockMap.put(event.getPlayer().getUniqueId(), ItemManager.nextItem(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.entityMap.get(event.getPlayer().getUniqueId()));
                String entityName = BossbarManager.formatItemName(ItemManager.entityMap.get(event.getPlayer().getUniqueId()).name()).replace("_", " ");
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " skipped " + entityName);
                if (ChainLogic.isChainMode()) {
                    ChainLogic.chainProgressMobHashMap.get(event.getPlayer().getUniqueId()).remove(0);
                }
                ItemManager.entityMap.put(event.getPlayer().getUniqueId(), ItemManager.nextMob(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.biomeMap.get(event.getPlayer().getUniqueId()));
                String biomeName = BossbarManager.formatItemName(ItemManager.biomeMap.get(event.getPlayer().getUniqueId()).name()).replace("_", " ");
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " skipped " + biomeName);
                if (ChainLogic.isChainMode()) {
                    ChainLogic.chainProgressBiomeHashMap.get(event.getPlayer().getUniqueId()).remove(0);
                }
                ItemManager.biomeMap.put(event.getPlayer().getUniqueId(), ItemManager.nextBiome(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                ItemManager.markedAsFinished(event.getPlayer(), ItemManager.advancementMap.get(event.getPlayer().getUniqueId()));
                String advancementName = ItemManager.advancementMap.get(event.getPlayer().getUniqueId()).getName();
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " skipped " + advancementName);
                if (ChainLogic.isChainMode()) {
                    ChainLogic.chainProgressAdvancementHashMap.get(event.getPlayer().getUniqueId()).remove(0);
                }
                ItemManager.advancementMap.put(event.getPlayer().getUniqueId(), ItemManager.nextAdvancement(event.getPlayer()));
                PointsManager.addPoint(event.getPlayer());
                NametagManager.updateNametag(event.getPlayer());
                BossbarManager.updateBossbar(event.getPlayer());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (ChallengeManager.getChallengeType() == null) return;
        if (!Timer.isRunning()) return;
        Player player = event.getPlayer();
        if (ExcludeCommand.excludedPlayers.contains(player.getUniqueId())) return;

        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {

            Block block = event.getTo().getBlock();
            Block blockBelow = block.getRelative(BlockFace.DOWN);

            BlockState blockState = block.getState();
            BlockState blockBelowState = blockBelow.getState();

            if (this.isSameBlockType(blockState, player) || this.isSameBlockType(blockBelowState, player)) {
                ItemManager.markedAsFinished(player, block.getType());
                Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " finished " + BossbarManager.formatItemName(ItemManager.blockMap.get(player.getUniqueId()).name()).replace("_", " "));
                if (ChainLogic.isChainMode()) {
                    ChainLogic.chainProgressBlockHashMap.get(event.getPlayer().getUniqueId()).remove(0);
                }
                ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                PointsManager.addPoint(player);
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
            Location playerLocation = player.getLocation();
            Biome biome = player.getWorld().getBiome(playerLocation);

            if (!biome.equals(ItemManager.biomeMap.get(player.getUniqueId()))) {
                return;
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " found " + BossbarManager.formatItemName(ItemManager.biomeMap.get(player.getUniqueId()).name()).replace("_", " "));
            ItemManager.markedAsFinished(player, biome);
            if (ChainLogic.isChainMode()) {
                ChainLogic.chainProgressBiomeHashMap.get(event.getPlayer().getUniqueId()).remove(0);
            }
            ItemManager.biomeMap.put(player.getUniqueId(), ItemManager.nextBiome(player));
            PointsManager.addPoint(player);
            NametagManager.updateNametag(player);
            BossbarManager.updateBossbar(player);
        }
    }
}