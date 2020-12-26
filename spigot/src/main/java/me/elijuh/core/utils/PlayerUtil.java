package me.elijuh.core.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static String getPrefix(Player player) {
        return PlaceholderAPI.setPlaceholders(player, "%vault_prefix%");
    }

    public static String getPrefixColor(Player player) {
        return PlaceholderAPI.setPlaceholders(player, "%vault_prefix_color%");
    }

    public static String getColoredName(Player player) {
        return getPrefixColor(player) + player.getName();
    }

    public static int getPing(Player p) {
        try {
            Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
