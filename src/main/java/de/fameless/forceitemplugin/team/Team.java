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
    private final Inventory backpack = Bukkit.createInventory(null, 9*3, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Team Backpack");

    public Team(int id, List<UUID> players) {
        this.id = id;
        this.players = players;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() { return points; }
    public Inventory getBackpackInv() { return backpack; }
    public int getId() { return id; }
    public List<UUID> getPlayers() { return players; }
    public void addPlayer(Player player) {
        players.add(player.getUniqueId());

    }
    public void removePlayer(Player player) { players.remove(player.getUniqueId()); }
    public void delete() { players.clear(); }
}