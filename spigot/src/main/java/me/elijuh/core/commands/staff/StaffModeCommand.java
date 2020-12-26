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

public class StaffModeCommand extends SpigotCommand {

    public StaffModeCommand() {
        super("staff", ImmutableList.of("staffmode", "sm", "mod", "modmode", "hackermode"), "core.staff");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target = p;

        if (Core.ID.toLowerCase().contains("hub")) {
            p.sendMessage(ChatUtil.color("&cStaff Mode is disabled in hub."));
            return;
        }

        if (args.length > 0) {
            if (!p.hasPermission("core.admin")) {
                p.sendMessage(ChatUtil.color("&cYou do not have permission to change the state of others."));
                return;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                return;
            }
        }
        StaffManager.toggleStaffMode(target);
        p.sendMessage(ChatUtil.color("&4&lStaff &8⏐ &7" + (target.equals(p) ? "Your " : target.getName() + "'s ") + "staff mode has been set to &f"
                + StaffUtil.isStaffMode(target) + "&7."));
    }
}
