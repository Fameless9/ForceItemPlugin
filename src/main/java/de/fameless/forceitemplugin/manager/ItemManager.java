package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.challenge.Listeners;
import de.fameless.forceitemplugin.challenge.SwitchItem;
import de.fameless.forceitemplugin.files.*;
import de.fameless.forceitemplugin.util.Advancement;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemManager {
    public static HashMap<UUID, Material> itemMap;
    public static HashMap<UUID, Material> blockMap;
    public static HashMap<UUID, EntityType> entityMap;
    public static HashMap<UUID, Biome> biomeMap;
    public static HashMap<UUID, Advancement> advancementMap;

    static {
        ItemManager.itemMap = new HashMap<>();
        ItemManager.blockMap = new HashMap<>();
        ItemManager.entityMap = new HashMap<>();
        ItemManager.biomeMap = new HashMap<>();
        ItemManager.advancementMap = new HashMap<>();
    }

    public static void resetProgress(Player player) {
        for (Material material : Material.values()) {
            if (ItemYML.getItemProgressConfig().contains(player.getName() + "." + material.name())) {
                ItemYML.getItemProgressConfig().set(player.getName() + "." + material.name(), false);
            }
            if (BlockYML.getBlockProgressConfig().contains(player.getName() + "." + material.name())) {
                BlockYML.getBlockProgressConfig().set(player.getName() + "." + material.name(), false);
            }
        }
        for (EntityType type : EntityType.values()) {
            if (MobYML.getMobProgressConfig().contains(player.getName() + "." + type.name())) {
                MobYML.getMobProgressConfig().set(player.getName() + "." + type.name(), false);
            }
        }
        for (Biome biome : Biome.values()) {
            if (BiomeYML.getBiomeProgressConfig().contains(player.getName() + "." + biome.name())) {
                BiomeYML.getBiomeProgressConfig().set(player.getName() + "." + biome.name(), false);
            }
        }
        for (Advancement advancement : Advancement.values()) {
            if (AdvancementYML.getAdvancementProgressConfig().contains(player.getName() + "." + advancement.name())) {
                AdvancementYML.getAdvancementProgressConfig().set(player.getName() + "." + advancement.name(), false);
            }
        }
        player.getInventory().setItem(8, Listeners.getSkipItem());
        player.getInventory().setItem(7, SwitchItem.getSwitchItem());
        ItemYML.saveItemConfig();
        BlockYML.saveBlockConfig();
        MobYML.saveMobConfig();
        BiomeYML.saveBiomeConfig();
        AdvancementYML.saveAdvancementConfig();
        PointsManager.setPoints(player, 0);
        if (ChallengeManager.getChallengeType() != null) {
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                ItemManager.entityMap.put(player.getUniqueId(), nextMob(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
                return;
            }
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                ItemManager.itemMap.put(player.getUniqueId(), nextItem(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
                return;
            }
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                ItemManager.blockMap.put(player.getUniqueId(), nextItem(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                ItemManager.biomeMap.put(player.getUniqueId(), nextBiome(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                ItemManager.advancementMap.put(player.getUniqueId(), nextAdvancement(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
            }
        }
        BossbarManager.updateBossbar(player);
        NametagManager.updateNametag(player);
    }

    public static void markedAsFinished(Player player, Material item) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            if (ItemYML.getItemProgressConfig().contains(player.getName() + "." + item.toString())) {
                ItemYML.getItemProgressConfig().set(player.getName() + "." + item.name(), true);
                ItemYML.saveItemConfig();
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK) && BlockYML.getBlockProgressConfig().contains(player.getName() + "." + item.toString())) {
            BlockYML.getBlockProgressConfig().set(player.getName() + "." + item.name(), true);
            BlockYML.saveBlockConfig();
        }
    }

    public static void markedAsFinished(Player player, EntityType entity) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB) && MobYML.getMobProgressConfig().contains(player.getName() + "." + entity.name())) {
            MobYML.getMobProgressConfig().set(player.getName() + "." + entity.name(), true);
            MobYML.saveMobConfig();
        }
    }

    public static void markedAsFinished(Player player, Biome biome) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME) && BiomeYML.getBiomeProgressConfig().contains(player.getName() + "." + biome.name())) {
            BiomeYML.getBiomeProgressConfig().set(player.getName() + "." + biome.name(), true);
            BiomeYML.saveBiomeConfig();
        }
    }

    public static void markedAsFinished(Player player, Advancement advancement) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT) && AdvancementYML.getAdvancementProgressConfig().contains(player.getName() + "." + advancement.name())) {
            AdvancementYML.getAdvancementProgressConfig().set(player.getName() + "." + advancement.name(), true);
            AdvancementYML.saveAdvancementConfig();
        }
    }

    public static boolean isFinished(Player player, Material material) {
        if (ChallengeManager.getChallengeType() == null) {
            return false;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            return ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name());
        }
        return ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK) && BlockYML.getBlockProgressConfig().getBoolean(player.getName() + "." + material.name());
    }

    public static boolean isFinished(Player player, EntityType type) {
        return ChallengeManager.getChallengeType() != null && ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB) && ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + type.name());
    }

    public static Material nextItem(Player player) {
        if (ChallengeManager.getChallengeType() == null) return null;
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {

            List<Material> materialList = new ArrayList<>();
            for (Material material : Material.values()) {
                if (ItemYML.getItemProgressConfig().contains(player.getName() + "." + material.name())) {
                    if (!ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name())) {
                        if (ItemYML.getExcludedItems().contains(material)) continue;
                        materialList.add(material);
                    }
                }
            }
            if (materialList.isEmpty()) {
                return null;
            }
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return materialList.get(random.nextInt(materialList.size()));
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {

            List<Material> blockList = new ArrayList<>();
            for (Material material : Material.values()) {
                if (BlockYML.getBlockProgressConfig().contains(player.getName() + "." + material.name())) {
                    if (!BlockYML.getBlockProgressConfig().getBoolean(player.getName() + "." + material.name())) {
                        if (BlockYML.getExcludedBlocks().contains(material)) continue;
                        blockList.add(material);
                    }
                }
            }

            if (blockList.isEmpty()) {
                return null;
            }
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return blockList.get(random.nextInt(blockList.size()));
        }
        return null;
    }

    public static Biome nextBiome(Player player) {
        if (ChallengeManager.getChallengeType() == null) return null;
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) return null;

        List<Biome> biomeList = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            if (BiomeYML.getBiomeProgressConfig().contains(player.getName() + "." + biome.name())) {
                if (!BiomeYML.getBiomeProgressConfig().getBoolean(player.getName() + "." + biome.name())) {
                    if (BiomeYML.getExcludedBiomes().contains(biome)) continue;
                    biomeList.add(biome);
                }
            }
        }

        if (biomeList.isEmpty()) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return biomeList.get(random.nextInt(biomeList.size()));
    }

    public static EntityType nextMob(Player player) {
        if (ChallengeManager.getChallengeType() == null) {
            return null;
        }
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
            return null;
        }

        List<EntityType> entityList = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (MobYML.getMobProgressConfig().contains(player.getName() + "." + type.name())) {
                if (!MobYML.getMobProgressConfig().getBoolean(player.getName() + "." + type.name())) {
                    if (MobYML.getExcludedEntities().contains(type)) continue;
                    entityList.add(type);
                }
            }
        }

        if (entityList.isEmpty()) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return entityList.get(random.nextInt(entityList.size()));
    }

    public static Advancement nextAdvancement(Player player) {
        if (ChallengeManager.getChallengeType() == null) {
            return null;
        }
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
            return null;
        }

        List<Advancement> advancementList = new ArrayList<>();
        for (Advancement advancement : Advancement.values()) {
            if (AdvancementYML.getAdvancementProgressConfig().contains(player.getName() + "." + advancement.name())) {
                if (!AdvancementYML.getAdvancementProgressConfig().getBoolean(player.getName() + "." + advancement.name())) {
                    if (AdvancementYML.getExcludedAdvancements().contains(advancement)) continue;
                    advancementList.add(advancement);
                }
            }
        }

        if (advancementList.isEmpty()) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return advancementList.get(random.nextInt(advancementList.size()));
    }
}