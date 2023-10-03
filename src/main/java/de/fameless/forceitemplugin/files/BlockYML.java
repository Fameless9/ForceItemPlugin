package de.fameless.forceitemplugin.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fameless.forceitemplugin.ForceBattlePlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BlockYML {

    private static File jsonFile;

    public static void setupItemFile() throws IOException {
        jsonFile = new File(ForceBattlePlugin.getInstance().getDataFolder(), "blockprogress.json");
        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
            JsonObject initialData = new JsonObject();
            initialData.addProperty("plugin", 1);
            saveJsonFile(initialData);
        }
    }

    public static void addEntry(Player player) throws IOException {

        setupItemFile();

        JsonObject rootObject = getRootObject();
        JsonObject playerObject = getPlayerObject(player);
        JsonObject materialObject = getMaterialObject(player);


        List<Material> materials = new ArrayList<>();

        for (Material material : Material.values()) {
            if (material.isSolid()) {
                if (getExcludedBlocks().contains(material)) continue;
                materials.add(material);
            }
        }

        for (Material material2 : materials) {
            materialObject.addProperty(material2.name(), false);
        }

        playerObject.add("materials", materialObject);
        rootObject.add(player.getName(), playerObject);

        try {
            FileWriter fileWriter = new FileWriter(jsonFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(rootObject, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Material> getExcludedBlocks() {
        List<Material> list = new ArrayList<>();

        for (Material material : Material.values()) {
            if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banners")) {
                if (material.name().endsWith("BANNER")) {
                    list.add(material);
                    continue;
                }
            }
            if (material.name().endsWith("CANDLE_CAKE")) {
                list.add(material);
                continue;
            }
            if (material.name().startsWith("POTTED")) {
                list.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("TORCH")) {
                list.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SIGN")) {
                list.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("HEAD")) {
                list.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("CORAL")) {
                list.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("BANNER")) {
                list.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SKULL")) {
                list.add(material);
                continue;
            }
            if (material.name().endsWith("STEM")) {
                list.add(material);
            }
        }

        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_blocks")) {
            if (Material.getMaterial(s) != null) {
                list.add(Material.getMaterial(s));
            }
        }
        return list;
    }

    public static JsonObject getRootObject() {
        JsonParser parser = new JsonParser();
        try {
            return parser.parse(new FileReader(jsonFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject getPlayerObject(Player player) {
        if (getRootObject().getAsJsonObject(player.getName()) == null) {
            return new JsonObject();
        }
        return getRootObject().getAsJsonObject(player.getName());
    }

    public static JsonObject getMaterialObject(Player player) {
        if (getPlayerObject(player).getAsJsonObject("materials") == null) {
            return new JsonObject();
        }
        return getPlayerObject(player).getAsJsonObject("materials");
    }

    public static void saveJsonFile(JsonObject data) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}