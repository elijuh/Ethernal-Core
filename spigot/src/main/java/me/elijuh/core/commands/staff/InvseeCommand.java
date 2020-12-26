package me.elijuh.core.commands.staff;

import com.google.common.collect.Lists;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class InvseeCommand extends SpigotCommand {

    public InvseeCommand() {
        super("invsee", Lists.newArrayList("inspect"), "core.invsee");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
            } else {
                p.openInventory(target.getInventory());
                p.playSound(p.getLocation(), Sound.CLICK, 1.0F, 1.0F);
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /invsee <player>"));
        }
    }
}
