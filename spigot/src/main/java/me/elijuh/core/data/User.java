package me.elijuh.core.data;

import lombok.Data;
import me.elijuh.core.Core;
import me.elijuh.core.manager.StaffManager;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Data
public class User {
    private final Player player;
    private final UserData userData;
    private String lastMessaged;

    public User(Player player) {
        this.player = player;
        this.userData = new UserData(this);
        Core.i().getDatabaseManager().updateData(this);
    }

    public void sendMessage(String s) {
        player.sendMessage(ChatUtil.color(s));
    }

    public boolean isStaff() {
        return player.hasPermission("core.staff");
    }

    public boolean isMuted() {
        return Core.i().getDatabaseManager().isPunished(player.getUniqueId(), Punishment.MUTE);
    }

    public void unload() {
        if (StaffUtil.isVanished(player)) {
            StaffManager.disableVanish(player);
        }

        if (StaffUtil.isStaffMode(player)) {
            StaffManager.disableStaffMode(player);
        }

        if (StaffUtil.isFrozen(player)) {
            StaffManager.unfreeze(player);
            BaseComponent base = new TextComponent(ChatUtil.color("&4" + player.getName() + " has logged out whilst frozen! "));
            BaseComponent ban = new TextComponent(ChatUtil.color("&a[Click to Ban]"));
            ban.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                    ChatUtil.color("&aClick here to Ban"))));
            ban.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ban " + player.getName() + " Logged out whilst frozen"));
            for (User user : Core.i().getUsers()) {
                if (user.isStaff()) {
                    user.sendMessage(" ");
                    user.getPlayer().spigot().sendMessage(base, ban);
                    user.sendMessage(" ");
                }
            }
        }

        if (player.getWalkSpeed() != 0.2F) {
            player.setWalkSpeed(0.2F);
        }

        StaffManager.contents.remove(player.getName());
        StaffManager.armor.remove(player.getName());

        Core.i().getUsers().remove(this);
    }

    public void kick(String... lines) {
        Bukkit.getScheduler().runTask(Core.i(), ()-> player.kickPlayer(ChatUtil.toLines(lines)));
    }

    public void clearchat() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            builder.append(" ");

            if (i % 50 == 0) {
                builder.delete(0, builder.length());
            }

            player.sendMessage(builder.toString());
        }
    }
}
