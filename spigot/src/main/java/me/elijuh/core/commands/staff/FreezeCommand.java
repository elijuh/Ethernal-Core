package me.elijuh.core.commands.staff;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.manager.StaffManager;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FreezeCommand extends SpigotCommand {

    public FreezeCommand() {
        super("freeze", ImmutableList.of("ss"), "core.freeze");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target;

        if (args.length > 0) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
            } else {
                if (StaffUtil.isFrozen(target)) {
                    StaffManager.unfreeze(target);
                } else {
                    StaffManager.freeze(target);
                }

                Core.log("&7[&e&o" + p.getName() + "&7: " +
                        (StaffUtil.isFrozen(target) ? "froze" : "unfroze") + " &e&o" + target.getName() + "&7]");
                p.sendMessage(ChatUtil.color("&4&lStaff &8⏐ &7You have " + (StaffUtil.isFrozen(target) ? "froze" : "unfroze") + " &f" + target.getName() + "&7."));
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /freeze <player>"));
        }
    }
}
