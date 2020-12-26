package me.elijuh.core.commands.core;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearCommand extends SpigotCommand {

    public ClearCommand() {
        super("clear", "core.clear");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target = p;

        if (args.length > 0) {
            if (!p.hasPermission("core.clear.others")) {
                p.sendMessage(ChatUtil.color("&cYou do not have permission to clear inventory of others."));
                return;
            }

            target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }
        }

        if (!target.equals(p)) {
            Core.log("&7[&e&o" + p.getName() + "&7: cleared inventory of &e&o" + target.getName() + "&7]");
            p.sendMessage(ChatUtil.color("&8» &7You have cleared the inventory of &c" + target.getName() + "&7."));
        } else {
            target.sendMessage(ChatUtil.color("&8» &7You have cleared your inventory."));
        }
        target.getInventory().clear();
        target.getInventory().setArmorContents(null);
    }
}
