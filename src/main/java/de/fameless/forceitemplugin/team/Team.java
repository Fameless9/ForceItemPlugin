package de.fameless.forceitemplugin.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

public class Team {

    private final int id;
    private int points;
    private final List<UUID> players;
    private final Inventory backpack;

    public Team(int id, List<UUID> players) {
        this.backpack = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Team Backpack");
        this.id = id;
        this.players = players;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Inventory getBackpackInv() {
        return backpack;
    }

    public int getId() {
        return id;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public void delete() {
        players.clear();
    }
}