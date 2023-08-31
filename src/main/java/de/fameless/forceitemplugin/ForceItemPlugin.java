package de.fameless.forceitemplugin;

import de.fameless.forceitemplugin.bStats.Metrics;
import de.fameless.forceitemplugin.manager.PointsManager;
import de.fameless.forceitemplugin.team.InviteReactCommand;
import de.fameless.forceitemplugin.team.TeamBackpack;
import de.fameless.forceitemplugin.team.TeamCommand;
import de.fameless.forceitemplugin.util.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;

public final class ForceItemPlugin extends JavaPlugin {

    private static ForceItemPlugin instance;

    private Timer timer;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        instance = this;

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

        timer = new Timer();

        getCommand("timer").setExecutor(timer);
        getCommand("skipitem").setExecutor(new SkipItemCommand());
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("invite").setExecutor(new InviteReactCommand());
        getCommand("backpack").setExecutor(new TeamBackpack());
        getCommand("exclude").setExecutor(new ExcludeCommand());

        Bukkit.getPluginManager().registerEvents(new Listeners(),this);

        int pluginId = 19683;
        Metrics metrics = new Metrics(this, pluginId);
    }

    public static ForceItemPlugin getInstance() {
        return instance;
    }
}