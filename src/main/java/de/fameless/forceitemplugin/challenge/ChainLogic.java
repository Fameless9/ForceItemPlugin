package de.fameless.forceitemplugin.challenge;

import de.fameless.forceitemplugin.files.*;
import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.ItemManager;
import de.fameless.forceitemplugin.manager.NametagManager;
import de.fameless.forceitemplugin.util.Advancement;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class ChainLogic {

    private static final List<Material> itemChainList = new ArrayList<>();
    private static final List<Material> blockChainList = new ArrayList<>();
    private static final List<EntityType> mobChainList = new ArrayList<>();
    private static final List<Biome> biomeChainList = new ArrayList<>();
    private static final List<Advancement> advancementChainList = new ArrayList<>();

    public static HashMap<UUID, List<Material>> chainProgressItemHashMap = new HashMap<>();
    public static HashMap<UUID, List<Material>> chainProgressBlockHashMap = new HashMap<>();
    public static HashMap<UUID, List<EntityType>> chainProgressMobHashMap = new HashMap<>();
    public static HashMap<UUID, List<Biome>> chainProgressBiomeHashMap = new HashMap<>();
    public static HashMap<UUID, List<Advancement>> chainProgressAdvancementHashMap = new HashMap<>();

    private static boolean ChainMode = false;

    public static void setupLists() {
        if (itemChainList.isEmpty()) {
            List<Material> items = new ArrayList<>();

            for (Material material : Material.values()) {
                if (ItemYML.getExcludedItems().contains(material)) continue;
                items.add(material);
            }

            Collections.shuffle(items);
            itemChainList.addAll(items);
        }

        if (blockChainList.isEmpty()) {
            List<Material> blocks = new ArrayList<>();

            for (Material material : Material.values()) {
                if (BlockYML.getExcludedBlocks().contains(material)) continue;
                blocks.add(material);
            }

            Collections.shuffle(blocks);
            blockChainList.addAll(blocks);
        }

        if (mobChainList.isEmpty()) {
            List<EntityType> mobs = new ArrayList<>();

            for (EntityType type : EntityType.values()) {
                if (MobYML.getExcludedEntities().contains(type)) continue;
                mobs.add(type);
            }

            Collections.shuffle(mobs);
            mobChainList.addAll(mobs);
        }

        if (blockChainList.isEmpty()) {
            List<Biome> biomes = new ArrayList<>();

            for (Biome biome : Biome.values()) {
                if (BiomeYML.getExcludedBiomes().contains(biome)) continue;
                biomes.add(biome);
            }

            Collections.shuffle(biomes);
            biomeChainList.addAll(biomes);
        }

        if (advancementChainList.isEmpty()) {
            List<Advancement> advancements = new ArrayList<>();

            for (Advancement advancement : Advancement.values()) {
                if (AdvancementYML.getExcludedAdvancements().contains(advancement)) continue;
                advancements.add(advancement);
            }

            Collections.shuffle(advancements);
            advancementChainList.addAll(advancements);
        }
    }

    public static void resetLists() {
        Collections.shuffle(itemChainList);
        Collections.shuffle(blockChainList);
        Collections.shuffle(mobChainList);
        Collections.shuffle(biomeChainList);
        Collections.shuffle(advancementChainList);

        for (Player player : Bukkit.getOnlinePlayers()) {
            ChainLogic.chainProgressItemHashMap.remove(player.getUniqueId());
            ChainLogic.chainProgressItemHashMap.put(player.getUniqueId(), itemChainList);

            ChainLogic.chainProgressBlockHashMap.remove(player.getUniqueId());
            ChainLogic.chainProgressBlockHashMap.put(player.getUniqueId(), blockChainList);

            ChainLogic.chainProgressMobHashMap.remove(player.getUniqueId());
            ChainLogic.chainProgressMobHashMap.put(player.getUniqueId(), mobChainList);

            ChainLogic.chainProgressBiomeHashMap.remove(player.getUniqueId());
            ChainLogic.chainProgressBiomeHashMap.put(player.getUniqueId(), biomeChainList);

            ChainLogic.chainProgressAdvancementHashMap.remove(player.getUniqueId());
            ChainLogic.chainProgressAdvancementHashMap.put(player.getUniqueId(), advancementChainList);

            if (!isChainMode()) return;

            if (ChallengeManager.getChallengeType() != null) {
                if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                    ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
                    BossbarManager.updateBossbar(player);
                    NametagManager.updateNametag(player);
                    return;
                }
                if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                    ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                    BossbarManager.updateBossbar(player);
                    NametagManager.updateNametag(player);
                    return;
                }
                if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                    ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                    BossbarManager.updateBossbar(player);
                    NametagManager.updateNametag(player);
                    return;
                }
                if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                    ItemManager.biomeMap.put(player.getUniqueId(), ItemManager.nextBiome(player));
                    BossbarManager.updateBossbar(player);
                    NametagManager.updateNametag(player);
                    return;
                }
                if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                    ItemManager.advancementMap.put(player.getUniqueId(), ItemManager.nextAdvancement(player));
                    BossbarManager.updateBossbar(player);
                    NametagManager.updateNametag(player);
                }
            }
        }
    }

    public static boolean isChainMode() {
        return ChainMode;
    }

    public static void setChainMode(boolean chainMode) {
        ChainMode = chainMode;
    }

    public static List<Material> getItemChainList() {
        return itemChainList;
    }

    public static List<Material> getBlockChainList() {
        return blockChainList;
    }

    public static List<EntityType> getMobChainList() {
        return mobChainList;
    }

    public static List<Biome> getBiomeChainList() {
        return biomeChainList;
    }

    public static List<Advancement> getAdvancementChainList() {
        return advancementChainList;
    }
}