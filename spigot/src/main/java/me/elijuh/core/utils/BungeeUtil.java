package me.elijuh.core.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.elijuh.core.Core;
import org.bukkit.entity.Player;

public class BungeeUtil {

    public static void send(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF(server);
        player.sendPluginMessage(Core.i(), "BungeeCord", out.toByteArray());
    }
}
