package me.elijuh.core.commands.punishments;

import com.google.common.collect.Lists;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.BanInfo;
import me.elijuh.core.manager.DatabaseManager;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryCommand extends SpigotCommand {
    DatabaseManager databaseManager;

    public HistoryCommand() {
        super("history", Lists.newArrayList("h"), "core.history");
        databaseManager = Core.i().getDatabaseManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length == 1) {
            if (databaseManager.hasData(args[0])) {
                List<BanInfo> history = databaseManager.getHistory(databaseManager.getUUID(args[0]));
                String name = ChatColor.stripColor(ChatUtil.color(databaseManager.getDisplay(args[0])));
                p.sendMessage(ChatUtil.color("&4&lStaff &8⏐ &7Showing history for &f" + name + "&7:"));
                if (history.isEmpty()) {
                    p.sendMessage(ChatUtil.color("&7&oEmpty!"));
                } else {
                    for (BanInfo info : history) {
                        p.sendMessage(ChatUtil.color("&6" + name + " &ewas banned by &6" +
                                info.getExecutor() + (info.isRemoved() || System.currentTimeMillis() - info.getExpiration() < 0 ?
                                " &a[Removed]" : " &c[Active]")));
                        p.sendMessage(ChatUtil.color("&eReason: &f" + info.getReason()));
                        p.sendMessage(" ");
                    }
                }
            } else {
                p.sendMessage(ChatUtil.color("&cThat player has never joined!"));
            }
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /history <player>"));
        }
    }
}
