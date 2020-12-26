package me.elijuh.core.tasks;

import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FrozenTask extends BukkitRunnable {
    private final Player p;
    private final String[] lines = {
            " ",
            "&f████&c█&f████",
            "&f███&c█&6█&c█&f███ &4&lDo NOT log out!",
            "&f██&c█&6█&0█&6█&c█&f██ &cYou have been frozen!",
            "&f██&c█&6█&0█&6█&c█&f██ &ePlease join the discord below",
            "&f█&c█&6██&0█&6██&c█&f█ &eAnd connect to the waiting room",
            "&f█&c█&6█████&c█&f█ &6&o" + Core.i().getConfig().getString("discord"),
            "&c█&6███&0█&6███&c█",
            "&c█████████",
            " "
    };

    public FrozenTask(Player p) {
        this.p = p;
        this.runTaskTimerAsynchronously(Core.i(), 0L, 200L);
    }

    @Override
    public void run() {
        if (p == null) {
            cancel();
            return;
        }
        if (StaffUtil.isFrozen(p)) {
            for (String s : lines) {
                p.sendMessage(ChatUtil.color(s));
            }
        } else {
            cancel();
        }
    }
}
