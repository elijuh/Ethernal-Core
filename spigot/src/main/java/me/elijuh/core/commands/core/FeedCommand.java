package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FeedCommand extends SpigotCommand {

    public FeedCommand() {
        super("feed", ImmutableList.of("eat"), "core.feed");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target = p;

        if (args.length > 0) {
            if (!p.hasPermission("core.feed.others")) {
                p.sendMessage(ChatUtil.color("&cYou cannot heal others."));
                return;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }
        }

        if (target.equals(p)) {
            p.sendMessage(ChatUtil.color("&4&lFeed &8⏐ &7You have been fed."));
        } else {
            p.sendMessage(ChatUtil.color("&4&lFeed &8⏐ &7You have fed &c" + target.getName() + "&7."));
        }

        target.setFoodLevel(40);
    }
}
