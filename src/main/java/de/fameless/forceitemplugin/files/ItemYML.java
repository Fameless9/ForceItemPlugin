package de.fameless.forceitemplugin.files;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fameless.forceitemplugin.ForceBattlePlugin;
import de.fameless.forceitemplugin.manager.BossbarManager;
import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.util.FormatTime;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemYML {

    private static File jsonFile;

    public static void setupItemFile() throws IOException {
        jsonFile = new File(ForceBattlePlugin.getInstance().getDataFolder(), "itemprogress.json");
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
            if (getExcludedItems().contains(material)) continue;
            materials.add(material);
        }

        for (Material material2 : materials) {
            materialObject.addProperty(material2.name(), false);
        }

        playerObject.add("materials", materialObject);
        rootObject.add(player.getName(), playerObject);

        saveJsonFile(rootObject);
    }

    public static List<Material> getExcludedItems() {

        List<Material> list = new ArrayList<>();
        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_items")) {
            if (Material.getMaterial(s) != null) {
                list.add(Material.getMaterial(s));
            }
        }

        for (Material material : Material.values()) {
            if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_spawn_eggs")) {
                if (material.name().endsWith("SPAWN_EGG")) {
                    list.add(material);
                    continue;
                }
            }
            if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_music_discs")) {
                if (material.name().contains("DISC")) {
                    list.add(material);
                    continue;
                }
            }
            if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banner_patterns")) {
                if (material.name().endsWith("BANNER_PATTERN")) {
                    list.add(material);
                    continue;
                }
            }
            if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banners")) {
                if (material.name().endsWith("BANNER")) {
                    list.add(material);
                    continue;
                }
            }
            if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_armor_templates")) {
                if (material.name().endsWith("TEMPLATE")) {
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

    public static final HashMap<UUID, List<ItemStack>> finishedItemsMap = new HashMap<>();

    public static List<ItemStack> getFinishedItemsFromJson(Player player) {
        List<ItemStack> list = new ArrayList<>();

        for (String key : getMaterialObject(player).keySet()) {
            if (getMaterialObject(player).get(key).getAsBoolean()) {
                list.add(new ItemStack(Material.getMaterial(key)));
            }
        }

        return list;
    }

    public static List<ItemStack> getFinishedItems(Player player) {
        return finishedItemsMap.get(player.getUniqueId());
    }

    public static void addFinishedItem(Player player, Material item) {
        ItemStack stack = new ItemStack(item);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + BossbarManager.formatItemName(item.name().replace("_", " ")));
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.GOLD + "Time" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + FormatTime.toFormatted(Timer.getTime()));
        meta.setLore(lore);

        stack.setItemMeta(meta);

        finishedItemsMap.get(player.getUniqueId()).add(stack);
    }

    public static void removeFinishedItem(Player player, Material item) {
        List<ItemStack> playerList = finishedItemsMap.get(player.getUniqueId());
        playerList.remove(new ItemStack(item));
        finishedItemsMap.put(player.getUniqueId(), playerList);
    }
}