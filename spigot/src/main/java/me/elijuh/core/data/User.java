package me.elijuh.core.data;

import lombok.Data;
import me.elijuh.core.Core;
import me.elijuh.core.manager.StaffManager;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
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

    public void unload() {
        Core.i().getDatabaseManager().updateData(this);

        if (StaffUtil.isVanished(player)) {
            StaffManager.disableVanish(player);
        }

        if (StaffUtil.isStaffMode(player)) {
            StaffManager.disableStaffMode(player);
        }

        if (StaffUtil.isFrozen(player)) {
            StaffManager.unfreeze(player);
            Core.log(" ", "core.freeze");
            Core.log("&4&l" + player.getName() + " has logged out whilst frozen!", "core.freeze");
            Core.log(" ", "core.freeze");
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
}
