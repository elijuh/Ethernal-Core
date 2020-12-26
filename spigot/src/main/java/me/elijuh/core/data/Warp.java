package me.elijuh.core.data;

import lombok.Getter;
import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
public class Warp {
    private final String name;
    private final Location location;
    private final String permission;

    public Warp(String name, Location location, String permission) {
        this.name = name.toLowerCase();
        this.location = location;
        this.permission = permission.isEmpty() || permission.startsWith("core.warp.") ? permission.toLowerCase() : "core.warp." + permission.toLowerCase();
    }

    public void save() {
        Core.warpManager.createWarp(this);
    }

    public void delete() {
        Core.warpManager.deleteWarp(this);
    }

    public void teleport(Player player) {
        player.teleport(this.location);
        player.sendMessage(ChatUtil.color("&6&lWarps &8» &7You have warped to &e" + ChatUtil.clean(this.name) + "&7."));
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
    }
}
