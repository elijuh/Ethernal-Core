package me.elijuh.core.commands.core;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.Warp;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WarpsCommand extends SpigotCommand {
    public WarpsCommand() {
        super("warps");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        List<Warp> warps = Core.warpManager.getWarps().stream()
                .filter(warp -> p.hasPermission(warp.getPermission()) || p.hasPermission("core.warp.*")).collect(Collectors.toList());
        p.sendMessage(ChatUtil.color("&4&lWarps &8⏐ " + (warps.isEmpty() ? "&7None" : "")));

        for (Warp warp : warps) {
            p.sendMessage(ChatUtil.color("&8» &7" + ChatUtil.clean(warp.getName())));
        }
    }
}
