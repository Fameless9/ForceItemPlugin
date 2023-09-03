package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.ForceItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MobYML {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupItemFile() throws IOException {
        file = new File(ForceItemPlugin.getInstance().getDataFolder(), "mobprogress.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void addEntry(Player player) throws IOException {

        List<EntityType> mobs = new ArrayList<>();
        List<EntityType> excludedMobs = new ArrayList<>();

        for (String s : ForceItemPlugin.getInstance().getConfig().getStringList("excluded_mobs")) {
            if (EntityType.valueOf(s) != null ) {
                excludedMobs.add(EntityType.valueOf(s));
            }
        }

        for (EntityType entity : EntityType.values()) {
            if (entity.equals(EntityType.ENDER_DRAGON)) continue;
            if (entity.equals(EntityType.PLAYER)) continue;
            if (excludedMobs.contains(entity)) continue;
            mobs.add(entity);
        }

        for (EntityType entity : mobs) {
            configuration.set(player.getName() + "." + entity.name(), false);
            saveMobConfig();
        }
    }

    public static YamlConfiguration getMobProgressConfig() {
        return configuration;
    }

    public static void saveMobConfig() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
