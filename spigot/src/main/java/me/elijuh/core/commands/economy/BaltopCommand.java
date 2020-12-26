package me.elijuh.core.commands.economy;

import me.elijuh.core.Core;
import me.elijuh.core.data.Pair;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BaltopCommand implements CommandExecutor {

    public BaltopCommand(Core plugin) {
        plugin.getCommand("baltop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        p.sendMessage(ChatUtil.color("&7Showing &4Balance Top&7:"));
        for (int i = Core.baltop.size() - 1; i > Math.max(Core.baltop.size() - 11, -1); i--) {
            int position = Core.baltop.size() - i;
            Pair<String, Double> pair = Core.baltop.get(i);
            p.sendMessage(ChatUtil.color("&c&l#" + position + " &7" + pair.getX() + ": &a$" + pair.getY()));
        }
        p.sendMessage(" ");
        return true;
    }
}
