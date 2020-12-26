package me.elijuh.core.hub;

import org.bukkit.scheduler.BukkitRunnable;

public class RefreshTask extends BukkitRunnable {
    HubScoreboard scoreboard;

    public RefreshTask(HubScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public void run() {
        if (scoreboard != null) {
            scoreboard.refresh();
        } else {
            cancel();
        }
    }
}
