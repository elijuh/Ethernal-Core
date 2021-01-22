package me.elijuh.core.commands.core;

import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.data.User;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class ReplyCommand extends SpigotCommand {

    public ReplyCommand() {
        super("reply", "r", "respond");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        User user = Core.i().getUser(p);
        if (user.isMuted()) {
            user.sendMessage(ChatUtil.color("&cYou cannot message whilst muted!"));
            return;
        }
        Player target;
        User targetUser;

        if (user.getLastMessaged() != null) {
            target = Bukkit.getPlayerExact(user.getLastMessaged());
            targetUser = Core.i().getUser(target);
            if (targetUser == null) {
                p.sendMessage(ChatUtil.color("&cThat player is no longer online!"));
            } else {
                if (!p.canSee(target)) {
                    p.sendMessage(ChatUtil.color("&cThat player is no longer online!"));
                    return;
                }
                if (args.length > 0) {
                    StringBuilder message = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        if (i != 0)
                            message.append(" ");
                        message.append(args[i]);
                    }
                    if (targetUser.getUserData().isMessageSoundEnabled()) {
                        target.playSound(target.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    }
                    target.sendMessage(ChatUtil.color("&7(From " + PlayerUtil.getPrefix(p) + p.getName() + "&7) " + message.toString()));
                    p.sendMessage(ChatUtil.color("&7(To " + PlayerUtil.getPrefix(target) + target.getName() + "&7) " + message.toString()));
                    targetUser.setLastMessaged(p.getName());
                    user.setLastMessaged(target.getName());
                } else {
                    p.sendMessage(ChatUtil.color("&cUsage: /r <message>"));
                }
            }
        } else {
            p.sendMessage(ChatUtil.color("&cYou have nobody to reply to!"));
        }
    }
}
