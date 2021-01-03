package me.elijuh.core.utils;

import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.entity.Player;

public class NametagUtil {

    public static void setPrefix(Player player, String prefix) {
        TabPlayer tab = TABAPI.getPlayer(player.getUniqueId());
        if (tab != null) {
            tab.setValueTemporarily(EnumProperty.TAGPREFIX, prefix);
        }
    }

    public static void removePrefix(Player player) {
        TabPlayer tab = TABAPI.getPlayer(player.getUniqueId());
        if (tab != null) {
            tab.removeTemporaryValue(EnumProperty.TAGPREFIX);
        }
    }
}
