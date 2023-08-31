package de.fameless.forceitemplugin.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class Backpack {

    public static HashMap<UUID, Inventory> backpackMap = new HashMap<>();

    public static Inventory getBackpack(Player player) {
        if (!backpackMap.containsKey(player.getUniqueId())) {
            return null;
        }
        return backpackMap.get(player.getUniqueId());
    }
}