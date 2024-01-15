package de.fameless.forceitemplugin;

import de.fameless.forceitemplugin.bStats.UpdateChecker;
import de.fameless.forceitemplugin.challenge.*;
import de.fameless.forceitemplugin.files.*;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.PointsManager;
import de.fameless.forceitemplugin.team.TeamBackpack;
import de.fameless.forceitemplugin.team.TeamCommand;
import de.fameless.forceitemplugin.team.TeamInviteReactCommand;
import de.fameless.forceitemplugin.timer.Timer;
import de.fameless.forceitemplugin.timer.TimerUI;
import de.fameless.forceitemplugin.util.ChallengeType;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.time.Duration;

public class ForceBattlePlugin extends JavaPlugin implements Listener {

    public static boolean isUpdated = true;
    private static ForceBattlePlugin instance;

    private final UpdateChecker updateChecker;

    public ForceBattlePlugin() {
        updateChecker = new UpdateChecker(112328, Duration.ofHours(2L));
    }

    public static ForceBattlePlugin getInstance() {
        return ForceBattlePlugin.instance;
    }

    public void onEnable() {

        saveDefaultConfig();

        ForceBattlePlugin.instance = this;
        updateChecker.checkForUpdates();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        try {
            PointsManager.setupPoints();
            MobYML.setupItemFile();
            ItemYML.setupItemFile();
            BlockYML.setupItemFile();
            BiomeYML.setupItemFile();
            AdvancementYML.setupItemFile();
        } catch (IOException e) {
            getLogger().severe("Failed to setup files. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Timer timer = new Timer();
        ChallengeCommand challengeCommand = new ChallengeCommand();
        ResetUI resetUI = new ResetUI();
        PointsUI pointsUI = new PointsUI();

        ChainLogic.setupLists();

        getCommand("timer").setExecutor(timer);
        getCommand("skipitem").setExecutor(new SkipItemCommand());
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("invite").setExecutor(new TeamInviteReactCommand());
        getCommand("backpack").setExecutor(new TeamBackpack());
        getCommand("exclude").setExecutor(new ExcludeCommand());
        getCommand("result").setExecutor(new SeeItemsCommand());
        getCommand("menu").setExecutor(challengeCommand);
        getCommand("reset").setExecutor(resetUI);
        getCommand("points").setExecutor(pointsUI);

        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getPluginManager().registerEvents(new SwitchItem(), this);
        Bukkit.getPluginManager().registerEvents(new TimerUI(), this);
        Bukkit.getPluginManager().registerEvents(new SeeItemsGUIListener(),this);
        Bukkit.getPluginManager().registerEvents(resetUI, this);
        Bukkit.getPluginManager().registerEvents(challengeCommand, this);
        Bukkit.getPluginManager().registerEvents(pointsUI, this);

        Metrics metrics = new Metrics(this, 19683);
        CustomChart chart = new SimplePie("challenge", () -> {
            if (ChallengeManager.getChallengeType() == null) {
                return "No challenge selected";
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ITEM)) {
                return "Force Item";
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BLOCK)) {
                return "Force Block";
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_MOB)) {
                return "Force Mob";
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_BIOME)) {
                return "Force Biome";
            } else if (ChallengeManager.getChallengeType().equals(ChallengeType.FORCE_ADVANCEMENT)) {
                return "Force Advancement";
            } else {
                return "No challenge selected";
            }
        });
        metrics.addCustomChart(chart);

        Listeners.checkForItem();
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("count_up")) {
            getConfig().set("time", Timer.getTime());
            saveConfig();
        }
    }
}