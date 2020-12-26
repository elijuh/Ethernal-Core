package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.BungeeUtil;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class HubCommand extends SpigotCommand {

    public HubCommand() {
        super("hub");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (Core.ID.toLowerCase().contains("hub")) {
            p.sendMessage(ChatUtil.color("&cYou are already connected to hub!"));
        } else {
            if (args.length > 0) {
                p.sendMessage(ChatUtil.color("&cUsage: /hub"));
            } else {
                BungeeUtil.send(p, "hub");
            }
        }
    }
}
