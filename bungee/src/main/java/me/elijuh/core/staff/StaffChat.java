package me.elijuh.core.staff;

import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.ServerUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffChat extends Command implements Listener {
    private final List<String> disabled = new ArrayList<>();

    public StaffChat() {
        super("staffchat", "core.staff", "sc", "schat");
        ProxyServer.getInstance().getPluginManager().registerListener(Core.getInstance(), this);
        ProxyServer.getInstance().getPluginManager().registerCommand(Core.getInstance(), this);
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length > 0) {
            StringBuilder message = new StringBuilder();
            for (String s : args) {
                message.append(s).append(" ");
            }
            send(p, message.toString().trim());
        } else {
            if (!disabled.remove(p.getName()))
                disabled.add(p.getName());

            p.sendMessage(TextComponent.fromLegacyText(ChatUtil.color("&8» &7You will " +
                    (disabled.contains(p.getName()) ? "no longer" : "now") + " see &cStaff Chat&7.")));
        }
    }

    @EventHandler
    public void on(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        if (e.getMessage().startsWith("#") && e.getMessage().length() > 1 && ((ProxiedPlayer) e.getSender()).hasPermission("core.staff")) {
            send((ProxiedPlayer)e.getSender(), e.getMessage().substring(1));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDisconnectEvent e) {
        disabled.remove(e.getPlayer().getName());
    }

    private void send(ProxiedPlayer p, String message) {
        if (disabled.contains(p.getName())) {
            p.sendMessage(TextComponent.fromLegacyText(ChatUtil.color("&cYou must enable your staff chat to use it!")));
            return;
        }

        BaseComponent prefix = new TextComponent(ChatUtil.color("&7[" + p.getServer().getInfo().getName() + "]"));
        prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatUtil.color("&6» &eClick to join this server."))));
        prefix.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + p.getServer().getInfo().getName()));

        List<BaseComponent> components = new ArrayList<>();
        components.add(prefix);
        components.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatUtil.color(" &b" + p.getName() + ": &7") + message)));

        BaseComponent[] finalMessage = components.toArray(new BaseComponent[0]);

        ServerUtil.getOnlineStaff().forEach(staff -> {
            if (!disabled.contains(staff.getName())) {
                staff.sendMessage(finalMessage);
            }
        });
    }
}
