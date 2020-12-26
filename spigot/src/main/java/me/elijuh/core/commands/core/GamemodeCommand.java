package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

public class GamemodeCommand extends SpigotCommand {

    public GamemodeCommand() {
        super("gamemode", ImmutableList.of("gm"), "core.gamemode");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        Player target = p;
        if (args.length > 0) {
            GameMode gamemode = null;
            try {
                gamemode = GameMode.valueOf(args[0].toUpperCase());
            } catch(Exception e) {
                try {
                    gamemode = GameMode.getByValue(Integer.parseInt(args[0]));
                } catch(Exception e2) {
                    for (GameMode mode : GameMode.values()) {
                        if (mode.toString().startsWith(args[0].toUpperCase().substring(0, 1))) {
                            gamemode = mode;
                            break;
                        }
                    }
                }
            }
            if (gamemode == null) {
                p.sendMessage(ChatUtil.color("&cCould not find gamemode: " + args[0]));
                p.sendMessage(ChatUtil.color("&cUsage: /gamemode <gamemode> [player]"));
                return;
            }
            if (args.length > 1) {
                if (!p.hasPermission("core.admin")) {
                    p.sendMessage(ChatUtil.color("&cYou do not have permission to change the state of others."));
                    return;
                }

                target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    p.sendMessage(ChatUtil.color("&cThat player is not online!"));
                    return;
                }
            }
            if (target.getGameMode().equals(gamemode)) {
                p.sendMessage(ChatUtil.color("&cError, " + (target.equals(p) ? "You are" : target.getName() + " is")
                        + " already in gamemode " + gamemode.toString() + "."));
            } else {
                target.setGameMode(gamemode);

                p.sendMessage(ChatUtil.color("&8» &7" + (target.equals(p) ? "Your " : target.getName() + "'s&e ") + "gamemode has been updated to &c"
                        + gamemode.toString().toLowerCase() + "&7."));
            }

        } else {
            p.sendMessage(ChatUtil.color("&cUsage: /gamemode <gamemode> [player]"));
        }
    }
}
