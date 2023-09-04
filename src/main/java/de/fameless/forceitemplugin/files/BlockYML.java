package de.fameless.forceitemplugin.files;

import de.fameless.forceitemplugin.ForceItemPlugin;
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
        file = new File(ForceItemPlugin.getInstance().getDataFolder(), "blockprogress.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void addEntry(Player player) throws IOException {

        List<Material> materials = new ArrayList<>();
        List<Material> excludedMaterials = new ArrayList<>();

        if (ForceItemPlugin.getInstance().getConfig().getBoolean("exclude_spawn_eggs")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("SPAWN_EGG")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceItemPlugin.getInstance().getConfig().getBoolean("exclude_music_discs")) {
            for (Material material : Material.values()) {
                if (material.name().contains("DISC")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceItemPlugin.getInstance().getConfig().getBoolean("exclude_banner_patterns")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("BANNER_PATTERN")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceItemPlugin.getInstance().getConfig().getBoolean("exclude_banners")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("BANNER")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceItemPlugin.getInstance().getConfig().getBoolean("exclude_armor_templates")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("TEMPLATE")) {
                    excludedMaterials.add(material);
                }
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
            if (material.name().contains("WALL") && material.name().contains("TORCH")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("SIGN")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("HEAD")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("CORAL")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("BANNER")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().contains("WALL") && material.name().contains("SKULL")) {
                excludedMaterials.add(material);
            }
        }

        for (Material material : Material.values()) {
            if (material.name().endsWith("STEM")) {
                excludedMaterials.add(material);
            }
        }

        for (String s : ForceItemPlugin.getInstance().getConfig().getStringList("excluded_blocks")) {
            if (Material.getMaterial(s) != null) {
                excludedMaterials.add(Material.getMaterial(s));
            }
        }

        for (Material material : Material.values()) {
            if (!material.isSolid()) continue;
            if (excludedMaterials.contains(material)) continue;
            materials.add(material);
        }

        for (Material material : materials) {
            configuration.set(player.getName() + "." + material.name(), false);
            saveBlockConfig();
        }
    }

    public static YamlConfiguration getBlockProgressConfig () {
        return configuration;
    }

    public static void saveBlockConfig() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}