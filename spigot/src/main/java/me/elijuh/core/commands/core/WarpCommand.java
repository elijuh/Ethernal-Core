package me.elijuh.core.commands.core;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.Warp;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpCommand extends SpigotCommand {

    public WarpCommand() {
        super("warp");
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
                p.sendMessage(ChatUtil.color("&7That warp does not exist."));
                p.sendMessage(ChatUtil.color("&7Type &c/warps &7for a list of warps."));
            } else {
                if (!p.hasPermission(warp.getPermission())) {
                    p.sendMessage(ChatUtil.color("&7That warp does not exist."));
                    p.sendMessage(ChatUtil.color("&7Type &c/warps &7for a list of warps."));
                } else warp.teleport(p);
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /warp <warp>"));
            p.sendMessage(ChatUtil.color("&7Type &c/warps &7for a list of warps."));
        }
    }
}
