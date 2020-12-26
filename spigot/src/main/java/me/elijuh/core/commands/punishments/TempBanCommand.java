package me.elijuh.core.commands.punishments;

import com.google.common.collect.Lists;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.Punishment;
import me.elijuh.core.data.redis.PunishmentInfo;
import me.elijuh.core.manager.DatabaseManager;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.MathUtil;
import me.elijuh.core.utils.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TempBanCommand extends SpigotCommand {
    DatabaseManager databaseManager;

    public TempBanCommand() {
        super("tempban", Lists.newArrayList(), "core.tempban");
        databaseManager = Core.i().getDatabaseManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 2) {
            if (!databaseManager.hasData(args[0])) {
                p.sendMessage(ChatUtil.color("&cThat player has never joined!"));
                return;
            }

            String executorDisplay = PlayerUtil.getColoredName(p);
            String punishedDisplay = databaseManager.getDisplay(args[0]);

            if (databaseManager.isPunished(args[0], Punishment.BAN)) {
                p.sendMessage(ChatUtil.color("&c" + args[0] + " is already banned!"));
                return;
            }

            long duration;

            try {
                duration = MathUtil.parseDate(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatUtil.color("&cPlease provide a valid date! example: 7d"));
                return;
            }

            StringBuilder reason = new StringBuilder(args[2]);

            for (int i = 3; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(Punishment.BAN, false, duration, reason.toString(), p.getName(),
                    args[0], executorDisplay, punishedDisplay);

            databaseManager.punish(info);
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /tempban <player> <time> <reason...>"));
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 2) {
            if (!databaseManager.hasData(args[0])) {
                sender.sendMessage(ChatUtil.color("&cThat player has never joined!"));
                return;
            }

            String executorDisplay = ChatUtil.color("&4&lConsole");
            String punishedDisplay = databaseManager.getDisplay(args[0]);

            if (databaseManager.isPunished(args[0], Punishment.BAN)) {
                sender.sendMessage(ChatUtil.color("&c" + args[0] + " is already banned!"));
                return;
            }

            long duration;

            try {
                duration = MathUtil.parseDate(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatUtil.color("&cPlease provide a valid date! example: 30d"));
                return;
            }

            StringBuilder reason = new StringBuilder(args[2]);

            for (int i = 3; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(Punishment.BAN, false, duration, reason.toString(), "Console",
                    args[0], executorDisplay, punishedDisplay);

            databaseManager.punish(info);
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /tempban <player> <time> <reason...>"));
        }
    }
}
