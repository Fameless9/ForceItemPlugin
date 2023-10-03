package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import de.fameless.forceitemplugin.challenge.ChainLogic;
import de.fameless.forceitemplugin.team.Team;
import de.fameless.forceitemplugin.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PointsManager {
    private static File file;
    private static YamlConfiguration configuration;

    public static void setupPoints() throws IOException {
        PointsManager.file = new File(ForceBattlePlugin.getInstance().getDataFolder(), "points.yml");
        if (!PointsManager.file.exists()) {
            PointsManager.file.createNewFile();
        }
        PointsManager.configuration = YamlConfiguration.loadConfiguration(PointsManager.file);
    }

    public static void setPoints(Player player, int points) {
        PointsManager.configuration.set(player.getName(), points);
        try {
            PointsManager.configuration.save(PointsManager.file);
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
                    Player player1 = Bukkit.getPlayer(uuid);
                    PointsManager.configuration.set(player1.getName(), newPoints);
                    try {
                        PointsManager.configuration.save(PointsManager.file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BossbarManager.updateBossbar(player1);
                    NametagManager.updateNametag(player1);
                    LeaderboardManager.adjustPoints(player1);
                    player1.playSound(player1, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1, 20);
                }
            }
        } else {
            PointsManager.configuration.set(player.getName(), newPoints);
            try {
                PointsManager.configuration.save(PointsManager.file);
            } catch (IOException e2) {
                throw new RuntimeException();
            }
            LeaderboardManager.adjustPoints(player);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1, 20);
        }
    }

    public static int getPoints(Player player) {
        if (PointsManager.configuration.contains(player.getName())) {
            return PointsManager.configuration.getInt(player.getName());
        }
        return 0;
    }
}