package me.elijuh.core.commands.economy;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.Pair;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class BaltopCommand extends SpigotCommand {

    public BaltopCommand() {
        super("baltop");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        p.sendMessage(ChatUtil.color("&7Showing &4Balance Top&7:"));
        for (int i = Core.baltop.size() - 1; i > Math.max(Core.baltop.size() - 11, -1); i--) {
            int position = Core.baltop.size() - i;
            Pair<String, Double> pair = Core.baltop.get(i);
            p.sendMessage(ChatUtil.color("&c&l#" + position + " &7" + pair.getX() + ": &a$" + ChatUtil.formatMoney(pair.getY())));
        }
        p.sendMessage(" ");
    }
}
