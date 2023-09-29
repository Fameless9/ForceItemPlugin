package de.fameless.forceitemplugin.files;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fameless.forceitemplugin.ForceBattlePlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MobYML {

    private static File jsonFile;

    public static void setupItemFile() throws IOException {
        jsonFile = new File(ForceBattlePlugin.getInstance().getDataFolder(), "mobprogress.json");
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
        JsonObject mobObject = getMobObject(player);

        List<EntityType> mobs = new ArrayList<>();

        for (EntityType entity : EntityType.values()) {
            if (getExcludedEntities().contains(entity)) continue;
            mobs.add(entity);
        }

        for (EntityType type : mobs) {
            if (getExcludedEntities().contains(type)) continue;
            mobObject.addProperty(type.name(), false);
        }

        playerObject.add("mobs", mobObject);
        rootObject.add(player.getName(), playerObject);

        saveJsonFile(rootObject);
    }

    public static List<EntityType> getExcludedEntities() {
        List<EntityType> list = new ArrayList<>();
        list.add(EntityType.ENDER_DRAGON);
        list.add(EntityType.PLAYER);
        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_mobs")) {
            if (EntityType.valueOf(s) != null) {
                list.add(EntityType.valueOf(s));
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

    public static JsonObject getMobObject(Player player) {
        if (getPlayerObject(player).getAsJsonObject("mobs") == null) {
            return new JsonObject();
        }
        return getPlayerObject(player).getAsJsonObject("mobs");
    }

    public static void saveJsonFile(JsonObject data) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}