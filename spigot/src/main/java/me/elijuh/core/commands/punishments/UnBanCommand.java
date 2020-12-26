package me.elijuh.core.commands.punishments;

import com.google.common.collect.Lists;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.Punishment;
import me.elijuh.core.data.redis.PunishmentInfo;
import me.elijuh.core.manager.DatabaseManager;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnBanCommand extends SpigotCommand {
    DatabaseManager databaseManager;

    public UnBanCommand() {
        super("unban", Lists.newArrayList(), "core.unban");
        databaseManager = Core.i().getDatabaseManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 1) {
            if (databaseManager.isPunished(args[0], Punishment.BAN)) {
                StringBuilder reason = new StringBuilder(args[1]);

                for (int i = 2; i < args.length; i++) {
                    reason.append(" ").append(args[i]);
                }

                String display = PlayerUtil.getColoredName(p);

                databaseManager.remove(databaseManager.getUUID(args[0]), new PunishmentInfo(
                        Punishment.BAN, true, -1, reason.toString(), p.getName(), args[0], display, databaseManager.getDisplay(args[0])));
            } else {
                p.sendMessage(ChatUtil.color("&cThat player is not banned!"));
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /unban <player> <reason...>"));
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (databaseManager.isPunished(args[0], Punishment.BAN)) {
                StringBuilder reason = new StringBuilder(args[1]);

                for (int i = 2; i < args.length; i++) {
                    reason.append(" ").append(args[i]);
                }

                String display = ChatUtil.color("&4&lConsole");

                databaseManager.remove(databaseManager.getUUID(args[0]), new PunishmentInfo(
                        Punishment.BAN, true, -1, reason.toString(), "Console", args[0], display, databaseManager.getDisplay(args[0])));
            } else {
                sender.sendMessage(ChatUtil.color("&cThat player is not banned!"));
            }
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /unban <player> <reason...>"));
        }
    }
}
