package me.elijuh.core.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.List;

@Getter
public abstract class SpigotCommand extends Command {
    public static final Core plugin = Core.i();
    String name, permission;
    List<String> aliases;

    public SpigotCommand(String name) {
        this(name, Lists.newArrayList(), null);
    }

    public SpigotCommand(String name, String... aliases) {
        this(name, Lists.newArrayList(aliases), null);
    }

    public SpigotCommand(String name, List<String> aliases, String permission) {
        super(name);
        setAliases(aliases);

        this.name = name;
        this.aliases = aliases;
        this.permission = permission;

        try{
            CommandMap map = (CommandMap) ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap").get(Bukkit.getServer());
            ReflectionUtil.unregisterCommands(map, getName(), getAliases());
            map.register(getName(), "ethernal", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            onConsole(sender, args);
            return false;
        }

        Player p = (Player) sender;
        if (permission != null && !p.hasPermission(permission)) {
            p.sendMessage(ChatUtil.color("&cNo permission."));
            return false;
        }

        try {
            onExecute(p, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (getPermission() != null) {
                if (!p.hasPermission(getPermission())) {
                    return ImmutableList.of();
                }
            }

            List<String> tabCompletion = onTabComplete(p, args);
            if (tabCompletion == null) {
                if (args.length == 0) {
                    final List<String> list = Lists.newArrayList();
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (p.canSee(all)) {
                            list.add(all.getName());
                        }
                    }
                    return list;
                } else {
                    final String lastWord = args[args.length - 1];
                    final List<String> list = Lists.newArrayList();

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (StringUtil.startsWithIgnoreCase(all.getName(), lastWord) && p.canSee(all)) {
                            list.add(all.getName());
                        }
                    }

                    return list;
                }
            }
            return tabCompletion;

        } else {
            return ImmutableList.of();
        }
    }

    public void onConsole(CommandSender sender, String[] args) {
        sender.sendMessage(ChatUtil.color("&cYou must be a player to execute this command."));
    }

    public abstract List<String> onTabComplete(Player p, String[] args);

    public abstract void onExecute(Player p, String[] args);

}
