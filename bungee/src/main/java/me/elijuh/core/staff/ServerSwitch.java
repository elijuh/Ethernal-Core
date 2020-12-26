package me.elijuh.core.staff;

import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.ServerUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class ServerSwitch implements Listener {
    private final Map<String, String> lastServer = new HashMap<>();

    public ServerSwitch() {
        ProxyServer.getInstance().getPluginManager().registerListener(Core.getInstance(), this);
    }

    @EventHandler
    public void on(ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        String server = p.getServer().getInfo().getName();
        if (p.hasPermission("core.staff")) {
            BaseComponent component = new TextComponent(ChatUtil.color("&4[Staff] &f" + p.getName() + " &7has connected to &f" + server
                    + (lastServer.containsKey(p.getName()) ? " &7from &f" + lastServer.get(p.getName()) + "&7." : "&7.")));

            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatUtil.color("&8» &fClick to join this server."))));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + server));

            for (ProxiedPlayer staff : ServerUtil.getOnlineStaff()) {
                staff.sendMessage(ChatMessageType.CHAT, component);
            }

            lastServer.put(p.getName(), server);
        }
    }

    @EventHandler
    public void on(PlayerDisconnectEvent e) {
        lastServer.remove(e.getPlayer().getName());

        ProxiedPlayer p = e.getPlayer();
        String server = p.getServer().getInfo().getName();
        if (p.hasPermission("core.staff")) {
            BaseComponent component = new TextComponent(ChatUtil.color("&4[Staff] &f" + p.getName() + " &7has disconnected from &f" + server + "&7."));

            for (ProxiedPlayer staff : ServerUtil.getOnlineStaff()) {
                staff.sendMessage(ChatMessageType.CHAT, component);
            }
        }
    }
}
