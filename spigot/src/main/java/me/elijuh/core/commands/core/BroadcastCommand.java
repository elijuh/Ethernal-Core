package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.User;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BroadcastCommand extends SpigotCommand {

    public BroadcastCommand() {
        super("broadcast", Lists.newArrayList(), "core.broadcast");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            StringBuilder message = new StringBuilder();
            for (String s : args) {
                message.append(" ").append(s);
            }
            for (User user : Core.i().getUsers()) {
                user.sendMessage(ChatUtil.color("&4&lEthernal &8» &r" + message.toString().trim()));
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /broadcast <message>"));
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 0) {
            StringBuilder message = new StringBuilder();
            for (String s : args) {
                message.append(s).append(" ");
            }
            for (User user : Core.i().getUsers()) {
                user.sendMessage(ChatUtil.color("&4&lEthernal &8» &r" + message.toString().trim()));
            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /broadcast <message>"));
        }
    }
}
