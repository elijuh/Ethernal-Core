package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportCommand extends SpigotCommand {

    public TeleportCommand() {
        super("teleport", ImmutableList.of("tp"), "core.teleport");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target1 = p;
        Player target2;

        if (args.length > 0) {
            target2 = Bukkit.getPlayer(args[0]);
            if (target2 == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }
            if (args.length > 1) {
                target1 = Bukkit.getPlayer(args[0]);
                target2 = Bukkit.getPlayer(args[1]);

                if (target1 == null || target2 == null) {
                    p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                    return;
                }
            }

            target1.teleport(target2);
            Core.log("&7[&e&o" + p.getName() + "&7: teleported " +
                    (target1.equals(p) ? "" : "&e&o" + target1.getName() + " ") + "&7to &e&o" + target2.getName() + "&7]");
            p.sendMessage(ChatUtil.color("&6» &7You have teleported " +
                    (target1.equals(p) ? "" : "&c" + target1.getName() + " ") + "&7to &c" + target2.getName() + "&7."));
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /tp <player> [player]"));
        }
    }
}
