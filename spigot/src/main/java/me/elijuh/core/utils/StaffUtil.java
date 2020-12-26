package me.elijuh.core.utils;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class StaffUtil {

    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean())
                return true;
        }
        return false;
    }

    public static boolean isStaffMode(Player player) {
        for (MetadataValue meta : player.getMetadata("staffmode")) {
            if (meta.asBoolean())
                return true;
        }
        return false;
    }

    public static boolean isFrozen(Player player) {
        for (MetadataValue meta : player.getMetadata("frozen")) {
            if (meta.asBoolean())
                return true;
        }
        return false;
    }
}
