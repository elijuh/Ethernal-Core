package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.Warp;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class SetwarpCommand extends SpigotCommand {

    public SetwarpCommand() {
        super("setwarp", ImmutableList.of(), "core.setwarp");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            Warp warp = new Warp(args[0], p.getLocation(), args.length > 1 ? args[1] : "");
            warp.save();
            p.sendMessage(ChatUtil.color("&4&lWarps &8⏐ &7You have created warp &c" + ChatUtil.clean(warp.getName()) + " &7at your location."));

            if (!warp.getPermission().equals("")) {
                p.sendMessage(ChatUtil.color("&8» &7With permission &c" + warp.getPermission()));
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /setwarp <name> [permission]"));
        }
    }
}
