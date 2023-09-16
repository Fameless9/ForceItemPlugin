package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.challenge.Advancement;
import de.fameless.forceitemplugin.challenge.ChallengeType;
import de.fameless.forceitemplugin.challenge.Listeners;
import de.fameless.forceitemplugin.challenge.SwitchItem;
import de.fameless.forceitemplugin.files.*;
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
    public final static HashMap<UUID, Material> itemMap = new HashMap<>();
    public final static HashMap<UUID, Material> blockMap = new HashMap<>();
    public final static HashMap<UUID, EntityType> entityMap = new HashMap<>();
    public final static HashMap<UUID, Biome> biomeMap = new HashMap<>();
    public final static HashMap<UUID, Advancement> advancementMap = new HashMap<>();

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

    public static Material nextItem(Player player) {
        if (ChallengeManager.getChallengeType() == null) {
            return null;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            List<Material> materialList = new ArrayList<>();
            for (Material material : Material.values()) {
                if (ItemYML.getItemProgressConfig().contains(player.getName() + "." + material.name())) {
                    if (!ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name())) {
                        materialList.add(material);
                    }
                }
            }
            if (materialList.isEmpty()) {
                return null;
            }
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return materialList.get(random.nextInt(materialList.size()));
        } else {
            if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                return null;
            }
            List<Material> blockList = new ArrayList<>();
            for (Material material : Material.values()) {
                if (BlockYML.getBlockProgressConfig().contains(player.getName() + "." + material.name())) {
                    if (!BlockYML.getBlockProgressConfig().getBoolean(player.getName() + "." + material.name())) {
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
    }

    public static Biome nextBiome(Player player) {
        if (ChallengeManager.getChallengeType() == null) {
            return null;
        }
        if (!ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
            return null;
        }
        List<Biome> biomeList = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            if (BiomeYML.getBiomeProgressConfig().contains(player.getName() + "." + biome.name())) {
                if (!BiomeYML.getBiomeProgressConfig().getBoolean(player.getName() + "." + biome.name())) {
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
        for (Advancement type : Advancement.values()) {
            if (AdvancementYML.getAdvancementProgressConfig().contains(player.getName() + "." + type.name())) {
                if (!AdvancementYML.getAdvancementProgressConfig().getBoolean(player.getName() + "." + type.name())) {
                    advancementList.add(type);
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