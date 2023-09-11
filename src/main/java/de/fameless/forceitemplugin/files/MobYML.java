package de.fameless.forceitemplugin.files;

import de.fameless.forceitemplugin.ForceBattlePlugin;
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
        MobYML.file = new File(ForceBattlePlugin.getInstance().getDataFolder(), "mobprogress.yml");
        if (!MobYML.file.exists()) {
            MobYML.file.createNewFile();
        }
        MobYML.configuration = YamlConfiguration.loadConfiguration(MobYML.file);
    }

    public static void addEntry(Player player) throws IOException {
        List<EntityType> mobs = new ArrayList<EntityType>();
        List<EntityType> excludedMobs = new ArrayList<EntityType>();
        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_mobs")) {
            for (EntityType entityType : EntityType.values()) {
                if (entityType.equals(EntityType.valueOf(s))) {
                    excludedMobs.add(EntityType.valueOf(s));
                }
            }
        }
        for (EntityType entity : EntityType.values()) {
            if (!entity.equals(EntityType.ENDER_DRAGON)) {
                if (!entity.equals(EntityType.PLAYER)) {
                    if (!excludedMobs.contains(entity)) {
                        mobs.add(entity);
                    }
                }
            }
        }
        for (EntityType entity2 : mobs) {
            MobYML.configuration.set(player.getName() + "." + entity2.name(), false);
            saveMobConfig();
        }
    }

    public static YamlConfiguration getMobProgressConfig() {
        return MobYML.configuration;
    }

    public static void saveMobConfig() {
        try {
            MobYML.configuration.save(MobYML.file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}