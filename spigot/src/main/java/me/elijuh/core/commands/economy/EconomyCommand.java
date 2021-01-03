package me.elijuh.core.commands.economy;

import com.google.common.collect.Lists;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class EconomyCommand extends SpigotCommand {
    private final Core plugin;

    public EconomyCommand() {
        super("economy", Lists.newArrayList("eco"), "core.economy");
        plugin = Core.i();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        OfflinePlayer target;

        if (args.length > 2) {
            target = Bukkit.getOfflinePlayer(args[1]);
            double amount;

            if (!plugin.getEconomy().hasAccount(target)) {
                p.sendMessage(ChatUtil.color("&cThat player does not have a profile!"));
                return;
            }

            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatUtil.color("&cPlease provide a valid amount!"));
                return;
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
                    p.sendMessage(ChatUtil.color("&cUsage: /eco <give | take | set> <player> <amount>"));
                }
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /eco <give | take | set> <player> <amount>"));
        }
    }
}
