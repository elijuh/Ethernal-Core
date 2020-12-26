package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.Warp;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class DelwarpCommand extends SpigotCommand {

    public DelwarpCommand() {
        super("delwarp", ImmutableList.of(), "core.delwarp");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            Warp warp = Core.warpManager.getWarp(args[0]);

            if (warp == null) {
                p.sendMessage(ChatUtil.color("&cThat warp does not exist."));
            } else {
                warp.delete();
                p.sendMessage(ChatUtil.color("&4&lWarps &8⏐ &7You have successfully deleted warp &c" + ChatUtil.clean(warp.getName()) + "&7."));
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /delwarp <name>"));
        }
    }
}
