package me.elijuh.core.commands.core;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.User;
import me.elijuh.core.data.redis.RequestInfo;
import me.elijuh.core.manager.RedisManager;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class RequestCommand extends SpigotCommand {
    private final RedisManager redisManager;

    public RequestCommand() {
        super("request");
        redisManager = Core.i().getRedisManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        User user = Core.i().getUser(p);
        if (user.getUserData().contains("last-request")) {
            long since = (System.currentTimeMillis() - (long) user.getUserData().get("last-request")) / 1000;
            if (since < 60) {
                user.sendMessage("&cYou are on a cooldown for " + (60 - since) + " more seconds.");
                return;
            }
        }

        if (args.length > 0) {
            StringBuilder reason = new StringBuilder((args[0]));
            for (int i = 1; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }
            user.getUserData().add("last-request", System.currentTimeMillis());
            p.sendMessage(ChatUtil.color("&aYour request has been sent, staff have been notified."));
            p.sendMessage(ChatUtil.color(" "));
            redisManager.getPubSubSender().async().publish("REQUEST",
                    redisManager.getGSON().toJson(new RequestInfo(Core.ID, p.getName(), reason.toString())));
        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /request <reason...>"));
        }
    }
}
