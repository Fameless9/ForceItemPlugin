package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.ForceItemPlugin;
import de.fameless.forceitemplugin.team.Team;
import de.fameless.forceitemplugin.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PointsManager {

    private static File file;
    private static YamlConfiguration configuration;

    public static void setupPoints() throws IOException {
        file = new File(ForceItemPlugin.getInstance().getDataFolder(), "points.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void setPoints(Player player, int points) {
        configuration.set(player.getName(), points);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LeaderboardManager.adjustPoints(player);
    }

    public static void addPoint(Player player) {
        int newPoints = getPoints(player) + 1;
        if (TeamManager.getTeam(player) != null) {
            Team team = TeamManager.getTeam(player);
            team.setPoints(newPoints);
            for (UUID uuid : team.getPlayers()) {
                if (Bukkit.getPlayer(uuid) != null) {
                    configuration.set(Bukkit.getPlayer(uuid).getName(), newPoints);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BossbarManager.updateBossbar(Bukkit.getPlayer(uuid));
                    NametagManager.updateNametag(Bukkit.getPlayer(uuid));
                    LeaderboardManager.adjustPoints(Bukkit.getPlayer(uuid));
                }
            }
        } else {
            configuration.set(player.getName(), newPoints);
            try {
                configuration.save(file);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            LeaderboardManager.adjustPoints(player);
        }
    }

    public static int getPoints(Player player) {
        if (configuration.contains(player.getName())) {
            return configuration.getInt(player.getName());
        }
        return 0;
    }
}