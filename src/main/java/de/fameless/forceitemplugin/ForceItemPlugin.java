package de.fameless.forceitemplugin;

import de.fameless.forceitemplugin.bStats.Metrics;
import de.fameless.forceitemplugin.bStats.UpdateChecker;
import de.fameless.forceitemplugin.manager.PointsManager;
import de.fameless.forceitemplugin.team.InviteReactCommand;
import de.fameless.forceitemplugin.team.TeamBackpack;
import de.fameless.forceitemplugin.team.TeamCommand;
import de.fameless.forceitemplugin.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.time.Duration;

public final class ForceItemPlugin extends JavaPlugin {

    private static ForceItemPlugin instance;

    private ChallengeCommand challengeCommand;
    private Timer timer;
    public static boolean isUpdated = true;
    private UpdateChecker updateChecker = new UpdateChecker(112328, Duration.ofHours(2));

    @Override
    public void onEnable() {

        saveDefaultConfig();

        instance = this;
        challengeCommand = new ChallengeCommand();

        updateChecker.checkForUpdates();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        try {
            PointsManager.setupPoints();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        try {
            ItemYML.setupItemFile();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        try {
            BlockYML.setupItemFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        timer = new Timer();

        getCommand("timer").setExecutor(timer);
        getCommand("skipitem").setExecutor(new SkipItemCommand());
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("invite").setExecutor(new InviteReactCommand());
        getCommand("backpack").setExecutor(new TeamBackpack());
        getCommand("exclude").setExecutor(new ExcludeCommand());
        getCommand("menu").setExecutor(challengeCommand);

        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
        Bukkit.getPluginManager().registerEvents(challengeCommand, this);

        int pluginId = 19683;
        Metrics metrics = new Metrics(this, pluginId);

        if (ChallengeCommand.isKeepInventory) {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.setGameRule(GameRule.KEEP_INVENTORY, true);
            }
        }
    }

    public static ForceItemPlugin getInstance() {
        return instance;
    }
}