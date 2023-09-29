package de.fameless.forceitemplugin.files;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fameless.forceitemplugin.ForceBattlePlugin;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BiomeYML {

    private static File jsonFile;

    public static void setupItemFile() throws IOException {
        jsonFile = new File(ForceBattlePlugin.getInstance().getDataFolder(), "biomeprogress.json");
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
        JsonObject biomeObject = getBiomeObject(player);

        List<Biome> biomes = new ArrayList<>();

        for (Biome biome : Biome.values()) {
            if (getExcludedBiomes().contains(biome)) continue;
            biomes.add(biome);
        }

        for (Biome biome : biomes) {
            biomeObject.addProperty(biome.name(), false);
        }

        playerObject.add("biomes", biomeObject);
        rootObject.add(player.getName(), playerObject);

        saveJsonFile(rootObject);
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

    public static JsonObject getBiomeObject(Player player) {
        if (getPlayerObject(player).getAsJsonObject("biomes") == null) {
            return new JsonObject();
        }
        return getPlayerObject(player).getAsJsonObject("biomes");
    }

    public static void saveJsonFile(JsonObject data) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Biome> getExcludedBiomes() {

        List<Biome> list = new ArrayList<>();

        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_biomes")) {
            for (Biome biome : Biome.values()) {
                if (Biome.valueOf(s) == biome) {
                    list.add(biome);
                }
            }
        }
        return list;
    }

}
