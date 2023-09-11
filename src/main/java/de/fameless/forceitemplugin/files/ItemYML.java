package de.fameless.forceitemplugin.files;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemYML {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupItemFile() throws IOException {
        ItemYML.file = new File(ForceBattlePlugin.getInstance().getDataFolder(), "itemprogress.yml");
        if (!ItemYML.file.exists()) {
            ItemYML.file.createNewFile();
        }
        ItemYML.configuration = YamlConfiguration.loadConfiguration(ItemYML.file);
    }

    public static void addEntry(Player player) throws IOException {
        List<Material> materials = new ArrayList<Material>();
        List<Material> excludedMaterials = new ArrayList<Material>();
        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_items")) {
            if (Material.getMaterial(s) != null) {
                excludedMaterials.add(Material.getMaterial(s));
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_spawn_eggs")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("SPAWN_EGG")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_music_discs")) {
            for (Material material : Material.values()) {
                if (material.name().contains("DISC")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banner_patterns")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("BANNER_PATTERN")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banners")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("BANNER")) {
                    excludedMaterials.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_armor_templates")) {
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

        for (Material material : Material.values()) {
            if (!excludedMaterials.contains(material)) {
                materials.add(material);
            }
        }

        for (Material material2 : materials) {
            ItemYML.configuration.set(player.getName() + "." + material2.toString(), false);
            ItemYML.configuration.save(ItemYML.file);
        }
    }

    public static YamlConfiguration getItemProgressConfig() {
        return ItemYML.configuration;
    }

    public static void saveItemConfig() {
        try {
            ItemYML.configuration.save(ItemYML.file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}