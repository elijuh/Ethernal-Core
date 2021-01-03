package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.MathUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class GcCommand extends SpigotCommand {
    public GcCommand() {
        super("gc", Lists.newArrayList(), "core.admin");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("run")) {
                p.sendMessage(ChatUtil.color("&cBefore: &8(&f" + getUsedRam() + "MB&7/&f" + getMaxRam() + "MB&8)"));
                System.gc();
                p.sendMessage(ChatUtil.color("&aAfter: &8(&f" + getUsedRam() + "MB&7/&f" + getMaxRam() + "MB&8)"));
            }
        } else {
            p.sendMessage(ChatUtil.color("&7Current memory usage: &8(&f" + getUsedRam() + "MB&7/&f" + getMaxRam() + "MB&8)"));
            p.sendMessage(ChatUtil.color("&8&oUse &7&o/gc run &8&oto run the garbage collector!"));
        }
    }

    private double getUsedRam() {
        return MathUtil.roundTo(getMaxRam() - getFreeRam(), 1);
    }

    private double getFreeRam() {
        return MathUtil.roundTo(Runtime.getRuntime().freeMemory() / 1000000D, 1);
    }

    private double getMaxRam() {
        return MathUtil.roundTo(Runtime.getRuntime().maxMemory() / 1000000D, 1);
    }
}
