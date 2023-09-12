package de.fameless.forceitemplugin.files;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BiomeYML {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupItemFile() throws IOException {
        file = new File(ForceBattlePlugin.getInstance().getDataFolder(), "biomeprogress.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void addEntry(Player player) throws IOException {

        List<Biome> biomes = new ArrayList<>();

        for (Biome biome : Biome.values()) {
            if (getExcludedBiomes().contains(biome)) continue;
            biomes.add(biome);
        }

        for (Biome biome : biomes) {
            configuration.set(player.getName() + "." + biome.name(), false);
            saveBiomeConfig();
        }
    }

    public static YamlConfiguration getBiomeProgressConfig() {
        return configuration;
    }

    public static void saveBiomeConfig() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException();
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
