package me.elijuh.core.commands.core;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.User;
import me.elijuh.core.data.redis.UserDataUpdateInfo;
import me.elijuh.core.manager.RedisManager;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundsCommand extends SpigotCommand {
    private final RedisManager redisManager;

    public SoundsCommand() {
        super("sounds", "togglesounds");
        redisManager = Core.i().getRedisManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        User user = Core.i().getUser(p);
        boolean current = true;

        if (user.getUserData().getStorage().getConfig().contains("message-sounds")) {
            current = user.getUserData().getStorage().getConfig().getBoolean("message-sounds");
        }

        redisManager.getPubSubSender().sync().publish("USERDATA",
                redisManager.getGSON().toJson(new UserDataUpdateInfo(p.getUniqueId().toString(), "message-sounds", !current)));

        p.sendMessage(ChatUtil.color("&7Messaging sounds have been " + (!current ? "&aEnabled" : "&cDisabled") + "&7."));
    }
}
