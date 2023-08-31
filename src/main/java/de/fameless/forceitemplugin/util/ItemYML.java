package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.ForceItemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemYML {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupItemFile() throws IOException {
        file = new File(ForceItemPlugin.getInstance().getDataFolder(), "itemprogress.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }
    public static void addEntry(Player player) throws IOException {

        List<Material> materials = new ArrayList<>();
        List<Material> excludedMaterials = new ArrayList<>();

        for (String s : ForceItemPlugin.getInstance().getConfig().getStringList("excluded_blocks")) {
            if (Material.getMaterial(s) != null ) {
                excludedMaterials.add(Material.getMaterial(s));
            }
        }

        for (Material material : Material.values()) {
            if (material.name().endsWith("SPAWN_EGG")) {
                excludedMaterials.add(material);
            }
        }

        /* for (Material material : Material.values()) {
            if (material.name().startsWith("MUSIC_DISC")) {
                excludedMaterials.add(material);
            }
        } */

        for (Material material : Material.values()) {
            if (material.name().endsWith("BANNER_PATTERN") || material.name().endsWith("BANNER")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().endsWith("TEMPLATE")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().endsWith("CANDLE_CAKE")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().startsWith("POTTED")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().endsWith("STEM")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (excludedMaterials.contains(material)) continue;
            materials.add(material);
        }

        for (Material material : materials) {
            configuration.set(player.getName() + "." + material.toString(), false);
            configuration.save(file);
        }
    }
    public static YamlConfiguration getItemProgressConfig() {
        return configuration;
    }
    public static void saveItemConfig() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}