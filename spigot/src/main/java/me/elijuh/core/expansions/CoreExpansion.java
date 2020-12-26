package me.elijuh.core.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CoreExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "core";
    }

    @Override
    public String getAuthor() {
        return "elijuh";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        switch (params.toLowerCase()) {
            case "health": {
                return String.valueOf(getHealth(p));
            }
            case "playercount": {
                return String.valueOf(Bukkit.getOnlinePlayers().stream().filter(p::canSee).count());
            }
            case "staffmode": {
                return ChatUtil.getState(StaffUtil.isStaffMode(p));
            }
            case "vanish": {
                return ChatUtil.getState(StaffUtil.isVanished(p));
            }
            case "chat_cooldown": {
                if (Core.i().getChatManager().getCooldown() > 0) {
                    return ChatUtil.color("&fSlowed " + Core.i().getChatManager().getCooldown() + "s");
                } else {
                    return ChatUtil.color("&fNot Slowed");
                }
            }
            default: {
                return "null";
            }
        }
    }

    private long getHealth(Player p) {
        double health = p.getHealth();
        health += ((CraftPlayer)p).getHandle().getAbsorptionHearts();
        return Math.round(health);
    }
}
