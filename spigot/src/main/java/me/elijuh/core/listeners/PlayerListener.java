package me.elijuh.core.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.elijuh.core.Core;
import me.elijuh.core.data.BanInfo;
import me.elijuh.core.data.ChatMessage;
import me.elijuh.core.data.Punishment;
import me.elijuh.core.data.User;
import me.elijuh.core.manager.ChatManager;
import me.elijuh.core.manager.DatabaseManager;
import me.elijuh.core.manager.StaffManager;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {
    DatabaseManager databaseManager;

    public PlayerListener(Core plugin) {
        databaseManager = plugin.getDatabaseManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent e) {
        databaseManager.updateData(e.getName(), e.getAddress().getHostAddress(), e.getUniqueId().toString(), "&7" + e.getName());
        if (databaseManager.isPunished(e.getUniqueId(), Punishment.BAN) || databaseManager.isIPBanned(e.getAddress().getHostAddress())) {
            BanInfo info = databaseManager.getBanInfo(e.getName());
            if (info != null) {
                boolean perm = info.getExpiration() == -1;
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                e.setKickMessage(ChatUtil.toLines(
                        " ",
                        "&4&lEthernal &8⏐ &fYou are " + (info.isIp() ? "IP Banned!" : "Banned!"),
                        " ",
                        "&7Reason: &f" + info.getReason(),
                        "&7Duration: &f" + (perm ? "Permanent" : ChatUtil.formatMillis(info.getExpiration() - System.currentTimeMillis())
                        )));
            }
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Core.i().getUsers().add(new User(e.getPlayer()));

        if (Core.i().getConfig().getBoolean("economy.enabled")) {
            if (!Core.i().getEconomy().hasAccount(e.getPlayer().getName())) {
                Core.i().getEconomy().createPlayerAccount(e.getPlayer().getName());
            }
        }

        if (e.getPlayer().hasPermission("core.staff")) {
            StaffManager.enableStaffMode(e.getPlayer());
            StaffManager.enableVanish(e.getPlayer(), 2L);
        } else {
            for (Player vanished : Bukkit.getOnlinePlayers()) {
                if (StaffUtil.isVanished(vanished)) {
                    e.getPlayer().hidePlayer(vanished);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        User user = Core.i().getUser(e.getPlayer());
        if (user != null) {
            user.unload();
        }
        e.setQuitMessage(null);
    }

    @EventHandler
    public void on(PlayerKickEvent e) {
        User user = Core.i().getUser(e.getPlayer());
        if (user != null) {
            user.unload();
        }
        e.setLeaveMessage(null);
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().split(" ")[0].contains(":") && !e.getPlayer().hasPermission("syntax.bypass")) {
            e.getPlayer().sendMessage(ChatUtil.color("&cColon syntax is not allowed!"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        e.setFormat(PlaceholderAPI.setPlaceholders(p, "%vault_prefix%") + "%1$s" + ChatUtil.color(" &8» &r") + "%2$s");

        if (!p.hasPermission("chat.bypass")) {
            ChatManager manager = Core.i().getChatManager();
            ChatMessage lastMessage = Core.i().getChatManager().getLastMessage(p);
            if (lastMessage != null) {
                if ((System.currentTimeMillis() - lastMessage.getTimestamp()) / 1000 < manager.getCooldown()) {
                    p.sendMessage(ChatUtil.color("&cChat is currently slowed, please wait " + manager.getPlayerCooldown(p) + "."));
                    e.setCancelled(true);
                } else if (e.getMessage().equalsIgnoreCase(lastMessage.getMessage())) {
                    p.sendMessage(ChatUtil.color("&cDouble posting is prohibited."));
                    e.setCancelled(true);
                } else {
                    manager.setLastMessage(p, e.getMessage());
                }
            } else {
               manager.setLastMessage(p, e.getMessage());
            }
        }
    }
}
