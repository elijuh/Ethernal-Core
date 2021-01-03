package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.Core;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatCommand extends SpigotCommand {
    private final List<String> SUBCOMMANDS = ImmutableList.of(
            "clear",
            "slow"
    );

    public ChatCommand() {
        super("chat", ImmutableList.of("chatmanager"), "core.admin");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        List<String> completion = new ArrayList<>();
        if (args.length == 1) {
            for (String s : SUBCOMMANDS) {
                if (StringUtil.startsWithIgnoreCase(s, args[0])) {
                    completion.add(s);
                }
            }
        } else {
            return ImmutableList.of();
        }
        return completion;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "slow": {
                    if (args.length == 2) {
                        try {
                            int cooldown = Integer.parseInt(args[1]);
                            p.sendMessage(ChatUtil.color("&aChat cooldown has been set to " + cooldown + " seconds."));
                            Core.i().getChatManager().setCooldown(cooldown);
                        } catch (NumberFormatException e) {
                            p.sendMessage(ChatUtil.color("&cPlease provide a valid integer!"));
                        }
                    } else {
                        p.sendMessage(ChatUtil.color("&cUsage: /chat slow <cooldown>"));
                    }
                    break;
                }
                case "clear": {
                    if (args.length == 1) {
                        p.performCommand("clearchat");
                    } else {
                        p.sendMessage(ChatUtil.color("&cUsage: /chat clear"));
                    }
                    break;
                }
            }
        } else {
            p.sendMessage(ChatUtil.color("&7&m                      &8[ &4&lChat Help &8]&7&m                             "));
            p.sendMessage(ChatUtil.color("&7- &f/chat clear"));
            p.sendMessage(ChatUtil.color("&7- &f/chat slow <cooldown>"));
            p.sendMessage(ChatUtil.color(" "));
            p.sendMessage(ChatUtil.color("&8(&7Current Cooldown: &f" + Core.i().getChatManager().getCooldown() + "s&8)"));
            p.sendMessage(ChatUtil.color("&7&m                                                                        "));
        }
    }
}
