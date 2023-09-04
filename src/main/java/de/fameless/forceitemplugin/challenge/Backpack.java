package de.fameless.forceitemplugin.challenge;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class Backpack {

    public static HashMap<UUID, Inventory> backpackMap = new HashMap<>();

    public static Inventory getBackpack(Player player) {
        return backpackMap.get(player.getUniqueId());
    }
}