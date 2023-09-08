package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.challenge.ChallengeType;
import de.fameless.forceitemplugin.challenge.Listeners;
import de.fameless.forceitemplugin.challenge.SwitchItem;
import de.fameless.forceitemplugin.files.BlockYML;
import de.fameless.forceitemplugin.files.ItemYML;
import de.fameless.forceitemplugin.files.MobYML;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemManager {

    public static HashMap<UUID, Material> itemMap = new HashMap<>();
    public static HashMap<UUID, Material> blockMap = new HashMap<>();
    public static HashMap<UUID, EntityType> entityMap = new HashMap<>();

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

        player.getInventory().remove(Listeners.getSkipItem());
        player.getInventory().setItem(8, Listeners.getSkipItem());
        player.getInventory().setItem(7, SwitchItem.getSwitchItem());

        ItemYML.saveItemConfig();
        BlockYML.saveBlockConfig();
        MobYML.saveMobConfig();
        PointsManager.setPoints(player, 0);

        if (ChallengeManager.getChallengeType() != null) {
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                entityMap.put(player.getUniqueId(), nextMob(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
                return;
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                itemMap.put(player.getUniqueId(), nextItem(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
                return;
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                blockMap.put(player.getUniqueId(), nextItem(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
            }
        }
        BossbarManager.updateBossbar(player);
        NametagManager.updateNametag(player);
    }

    public static void markedAsFinished(Player player, Material item) {
        if (ChallengeManager.getChallengeType() == null) return;
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            if (ItemYML.getItemProgressConfig().contains(player.getName() + "." + item.toString())) {
                ItemYML.getItemProgressConfig().set(player.getName() + "." + item.name(), true);
                ItemYML.saveItemConfig();
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
            if (BlockYML.getBlockProgressConfig().contains(player.getName() + "." + item.toString())) {
                BlockYML.getBlockProgressConfig().set(player.getName() + "." + item.name(), true);
                BlockYML.saveBlockConfig();
            }
        }
    }
    public static void markedAsFinished(Player player, EntityType entity) {
        if (ChallengeManager.getChallengeType() == null) return;
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
            if (MobYML.getMobProgressConfig().contains(player.getName() + "." + entity.name())) {
                MobYML.getMobProgressConfig().set(player.getName() + "." + entity.name(), true);
                MobYML.saveMobConfig();
            }
        }
    }
    public static boolean isFinished(Player player, Material material) {
        if (ChallengeManager.getChallengeType() == null) return false;
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            return ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name());
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
            return BlockYML.getBlockProgressConfig().getBoolean(player.getName() + "." + material.name());
        }
        return false;
    }

    public static boolean isFinished(Player player, EntityType type) {
        if (ChallengeManager.getChallengeType() == null) return false;
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
            return ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + type.name());
        }
        return false;
    }
    public static Material nextItem(Player player) {
        if (ChallengeManager.getChallengeType() == null) return null;
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            List<Material> materialList = new ArrayList<>();
            for (Material material : Material.values()) {
                if (!ItemYML.getItemProgressConfig().contains(player.getName() + "." + material.name())) continue;
                if (!ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name())) {
                    materialList.add(material);
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
                if (!BlockYML.getBlockProgressConfig().contains(player.getName() + "." + material.name())) continue;
                if (!BlockYML.getBlockProgressConfig().getBoolean(player.getName() + "." + material.name())) {
                    blockList.add(material);
                }
            }
            if (blockList.isEmpty()) {
                return null;
            }
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return blockList.get(random.nextInt(blockList.size()));
        } else {
            return null;
        }
    }
    public static EntityType nextMob(Player player) {
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
            List<EntityType> entityList = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (!MobYML.getMobProgressConfig().contains(player.getName() + "." + type.name())) continue;
                if (!MobYML.getMobProgressConfig().getBoolean(player.getName() + "." + type.name())) {
                    entityList.add(type);
                }
            }
            if (entityList.isEmpty()) {
                return null;
            }
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return entityList.get(random.nextInt(entityList.size()));
        } else {
            return null;
        }
    }
}