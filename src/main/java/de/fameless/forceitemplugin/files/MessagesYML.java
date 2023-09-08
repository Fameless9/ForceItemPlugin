package de.fameless.forceitemplugin.files;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesYML {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupMessageFile() throws IOException {
        file = new File(ForceBattlePlugin.getInstance().getDataFolder(), "language.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveFile() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YamlConfiguration getConfiguration() {
        return configuration;
    }
}