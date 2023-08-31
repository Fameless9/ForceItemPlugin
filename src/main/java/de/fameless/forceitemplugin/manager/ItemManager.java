package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.util.ItemYML;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ItemManager {

    public static HashMap<UUID, Material> itemMap = new HashMap<>();

    public static void markedAsFinished(Player player, Material item) {
        if (ItemYML.getItemProgressConfig().contains(player.getName() + "." + item.toString())) {
            ItemYML.getItemProgressConfig().set(player.getName() + "." + item.name(), true);
            ItemYML.saveItemConfig();
        }
    }
    public static boolean isFinished(Player player, Material material) {
        return ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name());
    }
    public static Material nextItem(Player player) {
        List<Material> materialList = new ArrayList<>();
        for (Material material : Material.values()) {
            if (!ItemYML.getItemProgressConfig().contains(player.getName() + "." + material.name())) continue;
            if (!ItemYML.getItemProgressConfig().getBoolean(player.getName() + "." + material.name())) {
                materialList.add(material);
            }
        }
        if (materialList.isEmpty()) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return materialList.get(random.nextInt(materialList.size()));
    }
}