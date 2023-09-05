package de.fameless.forceitemplugin;

import de.fameless.forceitemplugin.bStats.Metrics;
import de.fameless.forceitemplugin.bStats.UpdateChecker;
import de.fameless.forceitemplugin.challenge.*;
import de.fameless.forceitemplugin.files.BlockYML;
import de.fameless.forceitemplugin.files.ItemYML;
import de.fameless.forceitemplugin.files.MobYML;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.PointsManager;
import de.fameless.forceitemplugin.team.TeamBackpack;
import de.fameless.forceitemplugin.team.TeamCommand;
import de.fameless.forceitemplugin.team.TeamInviteReactCommand;
import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.timer.TimerUI;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.time.Duration;

public final class ForceItemPlugin extends JavaPlugin {

    private static ForceItemPlugin instance;

    public static boolean isUpdated = true;
    private final UpdateChecker updateChecker = new UpdateChecker(112328, Duration.ofHours(2));

    @Override
    public void onEnable() {

        saveDefaultConfig();

        instance = this;

        updateChecker.checkForUpdates();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        try {
            MobYML.setupItemFile();
            PointsManager.setupPoints();
            ItemYML.setupItemFile();
            BlockYML.setupItemFile();
        } catch (IOException e) {
            getLogger().severe("Couldn't setup files. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Timer timer = new Timer();
        ChallengeCommand challengeCommand = new ChallengeCommand();

        getCommand("timer").setExecutor(timer);
        getCommand("skipitem").setExecutor(new SkipItemCommand());
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("invite").setExecutor(new TeamInviteReactCommand());
        getCommand("backpack").setExecutor(new TeamBackpack());
        getCommand("exclude").setExecutor(new ExcludeCommand());
        getCommand("menu").setExecutor(challengeCommand);

        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
        Bukkit.getPluginManager().registerEvents(new SwitchItem(),this);
        Bukkit.getPluginManager().registerEvents(new TimerUI(), this);
        Bukkit.getPluginManager().registerEvents(challengeCommand, this);

        int pluginId = 19683;
        Metrics metrics = new Metrics(this, pluginId);
        Metrics.CustomChart chart = new Metrics.SimplePie("challenge", () -> {
           if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
               return "Force Item";
           } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
               return "Force Block";
           } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
               return "Force Mob";
           } else {
               return "No challenge selected";
           }
        });
        metrics.addCustomChart(chart);

        Listeners.checkForItem();

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