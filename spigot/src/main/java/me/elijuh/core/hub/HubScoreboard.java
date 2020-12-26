package me.elijuh.core.hub;

import com.google.common.collect.ImmutableList;
import me.clip.placeholderapi.PlaceholderAPI;
import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class HubScoreboard {
    private final Scoreboard previous;
    private final Player player;
    private final Scoreboard board;
    private final Objective objective;
    private final RefreshTask task;
    private static final List<String> LINES = ImmutableList.of(
            " ",
            "&4Rank:",
            "&8» %luckperms_primary_group_name%",
            " ",
            "&4Players:",
            "&8» &r%bungee_total%",
            " ",
            "&7" + Core.i().getConfig().getString("ip"),
            " "
    );

    public HubScoreboard(Player player) {
        this.previous = player.getScoreboard();
        this.player = player;
        this.board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        this.objective = this.board.registerNewObjective("sb", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(ChatUtil.color("&4&lEthernal &7| &fHub"));

        task = new RefreshTask(this);
    }

    public void enable() {
        int linecount = LINES.size();
        for(int i = 0; i < linecount; i++) {
            Team t = this.board.getTeam(String.valueOf(i)) != null ? this.board.getTeam(i + "") : this.board.registerNewTeam(String.valueOf(i));
            t.addEntry(String.valueOf(ChatColor.values()[i]));
            String line = PlaceholderAPI.setPlaceholders(player, LINES.get(i));

            if (i == 2) {
                line = line.substring(4);
                t.setPrefix(ChatUtil.color("&8» "));
                t.setSuffix(ChatUtil.color(line));
                objective.getScore(String.valueOf(ChatColor.values()[i])).setScore(linecount - i);
                continue;
            } else if (i == 0 || i == 8) {
                t.setPrefix(ChatUtil.color("&7&m---------"));
                t.setSuffix(ChatUtil.color("&7&m---------"));
                objective.getScore(String.valueOf(ChatColor.values()[i])).setScore(linecount - i);
                continue;
            }

            if (line.length() > 16) {
                line = line.substring(0, 15);
            }

            t.setPrefix(ChatUtil.color(line));
            objective.getScore(String.valueOf(ChatColor.values()[i])).setScore(linecount - i);
        }
        task.runTaskTimerAsynchronously(Core.i(), 0L, 100L);
        this.player.setScoreboard(this.board);
    }

    public void disable() {
        try {
            task.cancel();
        } catch(Exception ignored) {}
        this.player.setScoreboard(this.previous);
    }

    public void refresh() {
        this.board.getTeam("5").setPrefix(ChatUtil.color(PlaceholderAPI.setPlaceholders(player, LINES.get(5))));
    }
}
