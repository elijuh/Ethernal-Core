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

public class IPUnBanCommand extends SpigotCommand {
    DatabaseManager databaseManager;

    public IPUnBanCommand() {
        super("ipunban", Lists.newArrayList("unblacklist"), "core.ipunban");
        databaseManager = Core.i().getDatabaseManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 1) {
            String executorDisplay = PlayerUtil.getColoredName(p);
            String punishedDisplay = databaseManager.getDisplay(args[0]);

            if (!databaseManager.isIPBanned(databaseManager.getIP(databaseManager.getUUID(args[0])))) {
                p.sendMessage(ChatUtil.color("&cThat player is not ip banned!"));
                return;
            }

            StringBuilder reason = new StringBuilder(args[1]);

            for (int i = 2; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(Punishment.IPBAN, true, -1, reason.toString(), p.getName(),
                    args[0], executorDisplay, punishedDisplay);

            databaseManager.remove(databaseManager.getUUID(args[0]), info);
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /ipunban <player> <reason...>"));
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 1) {
            String executorDisplay = ChatUtil.color("&4&lConsole");
            String punishedDisplay = databaseManager.getDisplay(args[0]);

            if (!databaseManager.isIPBanned(databaseManager.getIP(databaseManager.getUUID(args[0])))) {
                sender.sendMessage(ChatUtil.color("&cThat player is not ip banned!"));
                return;
            }

            StringBuilder reason = new StringBuilder(args[1]);

            for (int i = 2; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(Punishment.IPBAN, true, -1, reason.toString(), "Console",
                    args[0], executorDisplay, punishedDisplay);

            databaseManager.remove(databaseManager.getUUID(args[0]), info);
        } else {
            sender.sendMessage(ChatUtil.color("&cUsage: /ipunban <player> <reason...>"));
        }
    }
}
