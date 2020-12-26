package me.elijuh.core.utils;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class ServerUtil {

    public static List<ProxiedPlayer> getOnlineStaff() {
        List<ProxiedPlayer> players = Lists.newArrayList();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("core.staff")) {
                players.add(player);
            }
        }
        return players;
    }
}
