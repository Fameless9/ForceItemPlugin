package de.fameless.forceitemplugin.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fameless.forceitemplugin.challenge.Listeners;
import de.fameless.forceitemplugin.challenge.SwitchItem;
import de.fameless.forceitemplugin.files.*;
import de.fameless.forceitemplugin.util.Advancement;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
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

        //  --- Resets for Blocks & Items ---
        JsonObject rootObject = ItemYML.getRootObject();
        JsonObject playerObject = ItemYML.getPlayerObject(player);
        JsonObject materialObject = ItemYML.getMaterialObject(player);

        JsonObject rootObjectBlock = BlockYML.getRootObject();
        JsonObject materialObjectBlock = BlockYML.getMaterialObject(player);
        JsonObject playerObjectBlock = BlockYML.getPlayerObject(player);

        for (Material material : Material.values()) {
            if (playerObject == null || material == null) return;
            if (materialObject.has(material.name())) {
                if (materialObject.has(material.name())) {
                    materialObject.addProperty(material.name(), false);
                }
            }
            if (playerObjectBlock == null) return;
            if (materialObjectBlock.has(material.name())) {
                if (materialObjectBlock.has(material.name())) {
                    materialObjectBlock.addProperty(material.name(), false);
                }
            }
        }

        playerObject.add("materials", materialObject);
        rootObject.add(player.getName(), playerObject);

        playerObjectBlock.add("materials", materialObjectBlock);
        rootObjectBlock.add(player.getName(), playerObjectBlock);

        ItemYML.saveJsonFile(rootObject);
        BlockYML.saveJsonFile(rootObjectBlock);


        //  --- Resets for Entities ---
        JsonObject rootObjectMob = MobYML.getRootObject();
        JsonObject playerObjectMob = MobYML.getPlayerObject(player);
        JsonObject mobObject = MobYML.getMobObject(player);

        for (EntityType type : EntityType.values()) {
            if (mobObject.has(type.name())) {
                mobObject.addProperty(type.name(), false);
            }
        }

        playerObjectMob.add("mobs", mobObject);
        rootObjectMob.add(player.getName(), playerObjectMob);

        MobYML.saveJsonFile(rootObjectMob);

        //  --- Resets for Biomes ---
        JsonObject rootObjectBiome = BiomeYML.getRootObject();
        JsonObject playerObjectBiome = BiomeYML.getPlayerObject(player);
        JsonObject biomeObject = BiomeYML.getBiomeObject(player);

        for (Biome biome : Biome.values()) {
            if (biomeObject.has(biome.name())) {
                biomeObject.addProperty(biome.name(), false);
            }
        }

        playerObjectBiome.add("biomes", biomeObject);
        rootObjectBiome.add(player.getName(), playerObjectBiome);

        BiomeYML.saveJsonFile(rootObjectBiome);

        //  --- Resets for Advancements ---
        JsonObject rootObjectAdv = AdvancementYML.getRootObject();
        JsonObject playerObjectAdv = AdvancementYML.getPlayerObject(player);
        JsonObject advancementObject = AdvancementYML.getAdvancementObject(player);

        for (Advancement advancement : Advancement.values()) {
            if (advancementObject.has(advancement.toString())) {
                advancementObject.addProperty(advancement.toString(), false);
            }
        }

        playerObjectAdv.add("advancements", advancementObject);
        rootObjectAdv.add(player.getName(), playerObjectAdv);

        AdvancementYML.saveJsonFile(rootObjectAdv);

        player.getInventory().setItem(8, Listeners.getSkipItem());
        player.getInventory().setItem(7, SwitchItem.getSwitchItem());
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
                return;
            }
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                ItemManager.biomeMap.put(player.getUniqueId(), nextBiome(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
                return;
            }
            if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                ItemManager.advancementMap.put(player.getUniqueId(), nextAdvancement(player));
                BossbarManager.updateBossbar(player);
                NametagManager.updateNametag(player);
            }
        }
    }

    public static void markedAsFinished(Player player, Material item) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
            JsonObject rootObject = ItemYML.getRootObject();
            JsonObject materialObject = ItemYML.getMaterialObject(player);
            JsonObject playerObject = ItemYML.getPlayerObject(player);

            if (materialObject.has(item.name())) {
                materialObject.addProperty(item.name(), true);

                playerObject.add("materials", materialObject);
                rootObject.add(player.getName(), playerObject);

                ItemYML.saveJsonFile(rootObject);
            }
        } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
            JsonObject rootObject = BlockYML.getRootObject();
            JsonObject materialObject = BlockYML.getMaterialObject(player);
            JsonObject playerObject = BlockYML.getPlayerObject(player);

            if (materialObject.has(item.name())) {
                materialObject.addProperty(item.name(), true);

                playerObject.add("materials", materialObject);
                rootObject.add(player.getName(), playerObject);

                BlockYML.saveJsonFile(rootObject);
            }
        }
    }

    public static void markedAsFinished(Player player, EntityType entity) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB) && MobYML.getMobObject(player).has(entity.name())) {
            JsonObject rootObject = MobYML.getRootObject();
            JsonObject playerObject = MobYML.getPlayerObject(player);
            JsonObject mobObject = MobYML.getMobObject(player);

            if (mobObject.has(entity.name())) {
                mobObject.addProperty(entity.name(), true);

                playerObject.add("mobs", mobObject);
                rootObject.add(player.getName(), playerObject);

                MobYML.saveJsonFile(rootObject);
            }
        }
    }

    public static void markedAsFinished(Player player, Biome biome) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME) && BiomeYML.getBiomeObject(player).has(biome.name())) {
            JsonObject rootObject = BiomeYML.getRootObject();
            JsonObject playerObject = BiomeYML.getPlayerObject(player);
            JsonObject biomeObject = BiomeYML.getBiomeObject(player);

            if (biomeObject.has(biome.name())) {
                biomeObject.addProperty(biome.name(), true);

                playerObject.add("biomes", biomeObject);
                rootObject.add(player.getName(), playerObject);

                BiomeYML.saveJsonFile(rootObject);
            }
        }
    }

    public static void markedAsFinished(Player player, Advancement advancement) {
        if (ChallengeManager.getChallengeType() == null) {
            return;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT) && AdvancementYML.getAdvancementObject(player).has(advancement.toString())) {
            JsonObject rootObject = AdvancementYML.getRootObject();
            JsonObject playerObject = AdvancementYML.getPlayerObject(player);
            JsonObject advancementObject = AdvancementYML.getAdvancementObject(player);

            if (advancementObject.has(advancement.toString())) {
                advancementObject.addProperty(advancement.toString(), true);

                playerObject.add("advancements", advancementObject);
                rootObject.add(player.getName(), playerObject);

                AdvancementYML.saveJsonFile(rootObject);
            }
        }
    }

    public static Material nextItem(Player player) {
        if (ChallengeManager.getChallengeType() == null) {
            return null;
        }
        if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {

            JsonObject materialObject = ItemYML.getMaterialObject(player);

            List<Material> materialList = new ArrayList<>();

            for (Material material : Material.values()) {
                if (materialObject.has(material.name())) {
                    if (!materialObject.get(material.name()).getAsBoolean()) {
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

            JsonObject materialObject = BlockYML.getMaterialObject(player);

            List<Material> blockList = new ArrayList<>();

            for (Material material : Material.values()) {
                if (materialObject.has(material.name())) {
                    if (!materialObject.get(material.name()).getAsBoolean()) {
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

        JsonObject biomeObject = BiomeYML.getBiomeObject(player);

        List<Biome> biomeList = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            if (biomeObject.has(biome.name())) {
                if (!biomeObject.get(biome.name()).getAsBoolean()) {
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

        JsonObject mobObject = MobYML.getMobObject(player);

        List<EntityType> entityList = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (mobObject.has(type.name())) {
                if (!mobObject.get(type.name()).getAsBoolean()) {
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

        JsonObject advObject = AdvancementYML.getAdvancementObject(player);

        List<Advancement> advancementList = new ArrayList<>();

        for (Advancement type : Advancement.values()) {
            if (advObject.has(type.name())) {
                if (!advObject.get(type.name()).getAsBoolean()) {
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