package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FlyCommand extends SpigotCommand {

    public FlyCommand() {
        super("fly", ImmutableList.of(), "core.fly");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target = p;

        if (args.length > 0) {
            if (!p.hasPermission("core.fly.others")) {
                p.sendMessage(ChatUtil.color("&cYou cannot toggle the state of others."));
                return;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }
        }

        target.setAllowFlight(!target.getAllowFlight());
        p.sendMessage(ChatUtil.color("&6» &7" + (target.equals(p) ? "Your" : target.getName()
                + "'s") + " flight has been set to &c" + target.getAllowFlight() + "."));
    }
}
