package me.elijuh.core.commands.core;

import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.PlayerUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PingCommand extends SpigotCommand {

    public PingCommand() {
        super("ping");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target = p;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online."));
                return;
            } else {
                if (StaffUtil.isVanished(target)) {
                    p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                    return;
                }
            }
        }
        p.sendMessage(ChatUtil.color("&8» &7" + target.getName() + "'s ping: &c" + PlayerUtil.getPing(target) + "ms"));
    }
}
