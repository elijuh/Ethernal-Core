package me.elijuh.core.commands.core.shortcut;

import me.elijuh.core.commands.SpigotCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class Gmc extends SpigotCommand {
    public Gmc() {
        super("gmc");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        p.performCommand("gamemode creative " + (args.length > 0 ? args[0] : ""));
    }
}
