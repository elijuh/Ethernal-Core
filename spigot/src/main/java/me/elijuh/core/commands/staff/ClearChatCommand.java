package me.elijuh.core.commands.staff;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.User;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.PlayerUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearChatCommand extends SpigotCommand {

    public ClearChatCommand() {
        super("clearchat", Lists.newArrayList("cc"), "core.clearchat");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length == 0) {
            for (User user : Core.i().getUsers()) {
                user.clearchat();
                user.sendMessage(ChatUtil.color("&aChat has been cleared by &r" + PlayerUtil.getColoredName(p)));
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /clearchat"));
        }
    }
}
