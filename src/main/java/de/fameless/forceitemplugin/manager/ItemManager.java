package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.util.BlockYML;
import de.fameless.forceitemplugin.util.ChallengeType;
import de.fameless.forceitemplugin.util.ItemYML;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ItemManager {

    public static HashMap<UUID, Material> itemMap = new HashMap<>();
    public static HashMap<UUID, Material> blockMap = new HashMap<>();

    public static void resetProgress(Player player) {
        for (Material material : Material.values()) {
            if (ItemYML.getItemProgressConfig().contains(player.getName() + "." + material.name())) {
                ItemYML.getItemProgressConfig().set(player.getName() + "." + material.name(), false);
            }
            if (BlockYML.getBlockProgressConfig().contains(player.getName() + "." + material.name())) {
                BlockYML.getBlockProgressConfig().set(player.getName() + "." + material.name(), false);
            }
        }
        ItemYML.saveItemConfig();
        BlockYML.saveBlockConfig();
        PointsManager.setPoints(player, 0);
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
    public static boolean isFinished(Player player, Material material) {
        if (ChallengeManager.getChallengeType() == null) return false;
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            return ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name());
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
            return BlockYML.getBlockProgressConfig().getBoolean(player.getName() + "." + material.name());
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
}