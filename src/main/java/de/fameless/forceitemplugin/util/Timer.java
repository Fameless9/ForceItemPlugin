package de.fameless.forceitemplugin.util;

import de.fameless.forceitemplugin.ForceItemPlugin;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.LeaderboardManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer implements CommandExecutor {

    public Timer() {
        if (ForceItemPlugin.getInstance().getConfig().getInt("challenge_duration") == -1) {
            setRunning(true);
        }
        run();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("forceitem.timer")) {
                player.sendMessage(ChatColor.RED + "Lacking permission: 'forceitem.timer'");
                return false;
            }
            if (args.length >= 1) {
                switch (args[0]){
                    case "toggle":
                        if (ChallengeManager.getChallengeType() == null) {
                        player.sendMessage(ChatColor.GOLD + "You need to select a challenge to start the timer. /menu");
                        return false;
                        }
                        if (ForceItemPlugin.getInstance().getConfig().getInt("challenge_duration") == -1) {
                            player.sendMessage(ChatColor.RED + "Time is set to infinite.");
                            return false;
                        }
                        if (isRunning()) {
                            setRunning(false);
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                players.sendTitle(ChatColor.RED + "Timer paused.","",20,40,20);
                            }
                            Bukkit.broadcastMessage(ChatColor.RED + "Timer has been paused.\n" + ChatColor.GOLD +
                                    "You can't collect any more items!");
                        }else {
                            setRunning(true);
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                players.sendTitle(ChatColor.RED + "Timer started.","",20,40,20);
                            }
                            Bukkit.broadcastMessage(ChatColor.GREEN + "Timer has been started.\n" + ChatColor.GOLD +
                                    "You can now start collecting your items!");
                        }
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid usage! Please use: /timer toggle");
                }
            }
        }
        return false;
    }

    private static int time;
    private static boolean running;

    public static int getTime() {
        return time;
    }

    public static void setTime(int time) {
        Timer.time = time;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Timer.running = running;
    }

    public static void run() {
        setTime(ForceItemPlugin.getInstance().getConfig().getInt("challenge_duration"));
        new BukkitRunnable() {
            @Override
            public void run() {
                sendActionbar();
                if (isRunning()) {
                    setTime(getTime() - 1);
                    if (time == 0) {
                        setRunning(false);
                        setTime(ForceItemPlugin.getInstance().getConfig().getInt("challenge_duration"));
                        LeaderboardManager.displayLeaderboard();
                    }
                }
            }
        }.runTaskTimer(ForceItemPlugin.getInstance(),0,20);
    }

    private static void sendActionbar() {
        for (Player p : Bukkit.getOnlinePlayers()) {

            int days = time / 86400;
            int hours = time / 3600 % 24;
            int minutes = time / 60 % 60;
            int seconds = time % 60;
            StringBuilder message = new StringBuilder();
            if (ForceItemPlugin.getInstance().getConfig().getInt("challenge_duration") == -1) {
                message.append("Infinite Time (Collect every item)");
            } else {
                if (days >= 1) {
                    message.append(days).append("d ");
                }
                if (hours >= 1) {
                    message.append(hours).append("h ");
                }
                if (minutes >= 1) {
                    message.append(minutes).append("m ");
                }
                message.append(seconds).append("s ");

                if (!isRunning()) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GOLD.toString() + ChatColor.ITALIC + message));
                } else {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GOLD.toString() + message));
                }
            }

        }
    }
}