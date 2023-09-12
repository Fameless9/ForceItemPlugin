package de.fameless.forceitemplugin.files;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockYML {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupItemFile() throws IOException {
        file = new File(ForceBattlePlugin.getInstance().getDataFolder(), "blockprogress.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void addEntry(Player player) throws IOException {
        List<Material> materials = new ArrayList<>();

        for (Material material : Material.values()) {
            if (material.isSolid()) {
                if (getExcludedBlocks().contains(material)) continue;
                materials.add(material);
            }
        }

        for (Material material2 : materials) {
            configuration.set(player.getName() + "." + material2.name(), false);
            saveBlockConfig();
        }
    }

    public static YamlConfiguration getBlockProgressConfig() {
        return configuration;
    }

    public static void saveBlockConfig() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static List<Material> getExcludedBlocks() {
        List<Material> list = new ArrayList<>();

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banners")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("BANNER")) {
                    list.add(material);
                }
            }
        }


        for (Material material : Material.values()) {
            if (material.name().endsWith("CANDLE_CAKE")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().startsWith("POTTED")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("TORCH")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("SIGN")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("HEAD")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("CORAL")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("BANNER")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("SKULL")) {
                list.add(material);
            }
        }

        for (Material material : Material.values()) {
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
}