package me.elijuh.core.commands.economy;

import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand implements CommandExecutor {
    private final Core plugin;

    public EconomyCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("economy").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        OfflinePlayer target;
        if (!p.hasPermission("core.economy")) {
            p.sendMessage(ChatUtil.color("&cNo permission."));
            return true;
        }

        if (args.length > 2) {
            target = Bukkit.getOfflinePlayer(args[1]);
            double amount;

            if (!plugin.getEconomy().hasAccount(target)) {
                p.sendMessage(ChatUtil.color("&cThat player does not have a profile!"));
                return true;
            }

            try {
                amount = Double.parseDouble(args[2]);
            } catch (Exception e) {
                p.sendMessage(ChatUtil.color("&cPlease provide a valid amount!"));
                return true;
            }

            switch(args[0].toLowerCase()) {
                case "give": {
                    plugin.getEconomy().depositPlayer(target, amount);
                    p.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7Balance of " + target.getName() + " has been updated to &a$" +
                            plugin.getEconomy().getBalance(target)));
                    break;
                }
                case "take": {
                    plugin.getEconomy().withdrawPlayer(target, amount);
                    p.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7Balance of " + target.getName() + " has been updated to &a$" +
                            plugin.getEconomy().getBalance(target)));
                    break;
                }
                case "set": {
                    plugin.getEconomy().withdrawPlayer(target, plugin.getEconomy().getBalance(target));
                    plugin.getEconomy().depositPlayer(target, amount);
                    p.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7Balance of " + target.getName() + " has been updated to &a$" +
                            plugin.getEconomy().getBalance(target)));

                    break;
                }
                default: {
                    p.sendMessage(ChatUtil.color("&cUsage: /" + label + " <give | take | set> <player> <amount>"));
                }
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /" + label + " <give | take | set> <player> <amount>"));
        }
        return true;
    }
}
