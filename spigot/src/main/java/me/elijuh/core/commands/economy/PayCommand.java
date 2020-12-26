package me.elijuh.core.commands.economy;

import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    public PayCommand(Core plugin) {
        plugin.getCommand("pay").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

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
                } catch (Exception e) {
                    p.sendMessage(ChatUtil.color("&cPlease provide a valid amount!"));
                    return true;
                }
                p.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7You have sent &a$" + amount + " &7to &f" + target.getName() + "."));
                target.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7You have recieved &a$" + amount + " &7from &f" + p.getName() + "."));
                target.playSound(target.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                Core.i().getEconomy().withdrawPlayer(p, amount);
                Core.i().getEconomy().depositPlayer(target, amount);
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /pay <player> <amount>"));
        }

        return true;
    }
}
