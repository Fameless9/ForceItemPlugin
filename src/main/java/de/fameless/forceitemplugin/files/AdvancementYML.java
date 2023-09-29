package de.fameless.forceitemplugin.files;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fameless.forceitemplugin.ForceBattlePlugin;
import de.fameless.forceitemplugin.util.Advancement;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdvancementYML {

    private static File jsonFile;

    public static void setupItemFile() throws IOException {
        jsonFile = new File(ForceBattlePlugin.getInstance().getDataFolder(), "advancementprogress.json");
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
        JsonObject advancementObject = getAdvancementObject(player);

        List<Advancement> advancements = new ArrayList<>();

        for (Advancement advancement : Advancement.values()) {
            if (getExcludedAdvancements().contains(advancement)) continue;
            advancements.add(advancement);
        }

        for (Advancement advancement : advancements) {
            advancementObject.addProperty(advancement.toString(), false);
        }

        playerObject.add("advancements", advancementObject);
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

    public static JsonObject getAdvancementObject(Player player) {
        if (getPlayerObject(player).getAsJsonObject("advancements") == null) {
            return new JsonObject();
        }
        return getPlayerObject(player).getAsJsonObject("advancements");
    }

    public static void saveJsonFile(JsonObject data) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Advancement> getExcludedAdvancements() {
        List<Advancement> list = new ArrayList<>();

        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_advancements")) {
            for (Advancement advancement : Advancement.values()) {
                if (Advancement.valueOf(s) == advancement) {
                    list.add(advancement);
                }
            }
        }
        return list;
    }

}
