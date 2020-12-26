package me.elijuh.core.commands.core;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends SpigotCommand {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("set") && p.hasPermission("core.admin")) {
                Core.setSpawn(p.getLocation());
                p.sendMessage(Core.prefix + ChatUtil.color( "&c/Spawn &7has been updated to your location."));
            } else if (args[0].equalsIgnoreCase("reload") && p.hasPermission("core.admin")) {
                Core.spawn = Core.getSpawn();
                p.sendMessage(Core.prefix + ChatUtil.color( "&c/Spawn &7has been reloaded from config."));
            } else {
                p.sendMessage(ChatUtil.color("&cUsage: /spawn" + (p.hasPermission("core.admin") ? " [set | reload]" : "")));
            }
        } else {
            p.teleport(Core.spawn);
        }
    }
}
