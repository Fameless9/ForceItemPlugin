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

        for (Material material : Material.values()) {
            if (getExcludedItems().contains(material)) continue;
            materials.add(material);
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

    public static List<Material> getExcludedItems() {

        List<Material> list = new ArrayList<>();
        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_items")) {
            if (Material.getMaterial(s) != null) {
                list.add(Material.getMaterial(s));
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_spawn_eggs")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("SPAWN_EGG")) {
                    list.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_music_discs")) {
            for (Material material : Material.values()) {
                if (material.name().contains("DISC")) {
                    list.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banner_patterns")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("BANNER_PATTERN")) {
                    list.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_banners")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("BANNER")) {
                    list.add(material);
                }
            }
        }

        if (ForceBattlePlugin.getInstance().getConfig().getBoolean("exclude_armor_templates")) {
            for (Material material : Material.values()) {
                if (material.name().endsWith("TEMPLATE")) {
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

        return list;
    }
}