package de.fameless.forceitemplugin.files;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import de.fameless.forceitemplugin.challenge.Advancement;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvancementYML {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupItemFile() throws IOException {
        file = new File(ForceBattlePlugin.getInstance().getDataFolder(), "advancementprogress.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void addEntry(Player player) {

        List<Advancement> advancements = new ArrayList<>();
        List<Advancement> excludedAdvancements = new ArrayList<>();

        for (String s : ForceBattlePlugin.getInstance().getConfig().getStringList("excluded_advancements")) {
            for (Advancement advancement : Advancement.values()) {
                if (Advancement.valueOf(s) == advancement) {
                    excludedAdvancements.add(advancement);
                }
            }
        }

        for (Advancement advancement : Advancement.values()) {
            if (excludedAdvancements.contains(advancement)) continue;
            advancements.add(advancement);
        }

        for (Advancement advancement : advancements) {
            configuration.set(player.getName() + "." + advancement.name(), false);
            saveAdvancementConfig();
        }
    }

    public static YamlConfiguration getAdvancementProgressConfig() {
        return configuration;
    }

    public static void saveAdvancementConfig() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
