package me.elijuh.core.commands.economy;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class PayCommand extends SpigotCommand {

    public PayCommand() {
        super("pay");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
            } else if (target == p) {
                p.sendMessage(ChatUtil.color("&cYou can't pay yourself!"));
            } else {
                if (StaffUtil.isVanished(target)) {
                    p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[1]);
                    p.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7You have sent &a$" + amount + " &7to &f" + target.getName() + "."));
                    target.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7You have recieved &a$" + amount + " &7from &f" + p.getName() + "."));
                    target.playSound(target.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    Core.i().getEconomy().withdrawPlayer(p, amount);
                    Core.i().getEconomy().depositPlayer(target, amount);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatUtil.color("&cPlease provide a valid amount!"));
                }
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /pay <player> <amount>"));
        }
    }
}
