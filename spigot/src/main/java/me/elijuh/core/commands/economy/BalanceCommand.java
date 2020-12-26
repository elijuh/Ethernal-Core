package me.elijuh.core.commands.economy;

import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.manager.CoreEconomy;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

public class BalanceCommand extends SpigotCommand {
    private static final NumberFormat nf = NumberFormat.getInstance();
    private final CoreEconomy economy;

    public BalanceCommand() {
        super("balance", "bal", "money", "$", "bands", "worth");
        economy = plugin.getEconomy();
    }


    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        String target = args.length > 0 ? args[0] : p.getName();
        if (economy.hasAccount(target)) {
            p.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7Balance of " +
                    Bukkit.getOfflinePlayer(target).getName() + ": &a$" + nf.format(economy.getBalance(target))));
        } else {
            p.sendMessage(ChatUtil.color("&4&lEconomy &8⏐ &7That player does not exist!"));
        }
    }
}
