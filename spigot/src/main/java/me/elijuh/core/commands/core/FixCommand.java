package me.elijuh.core.commands.core;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.commands.SpigotCommand;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixCommand extends SpigotCommand {

    public FixCommand() {
        super("fix", ImmutableList.of("repair"), "core.fix");
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("hand")) {
                repair(p);
            } else if (args[0].equalsIgnoreCase("all")) {
                if (p.hasPermission("core.fix.all")) {
                    repairAll(p);
                } else {
                    p.sendMessage(ChatUtil.color("&4&lFix &8⏐ &7You do not have permission to fix all, please use &c/fix hand&7."));
                }
            } else {
                p.sendMessage(ChatUtil.color("&cUsage: /fix [hand | all]"));
            }
        } else {
            repair(p);
        }
    }

    private void repair(Player p) {
        final ItemStack item = p.getItemInHand();

        if (item == null) {
            p.sendMessage(ChatUtil.color("&4&lFix &8⏐ &7You must have an item in your hand."));
            return;
        }

        final Material material = item.getType();

        if (!material.isBlock() && material.getMaxDurability() > 0 && item.getDurability() != 0) {
            item.setDurability((short)0);
            p.sendMessage(ChatUtil.color("&4&lFix &8⏐ &7You have repaired your &c"
                    + ChatUtil.clean(p.getItemInHand().getType().toString()) + "&7."));
        } else {
            p.sendMessage(ChatUtil.color("&4&lFix &8⏐ &7This item cannot be repaired."));
        }
    }

    private void repairAll(Player p) {
        final Map<Material, Integer> repaired = new HashMap<>();
        for (int i = 0; i < 40; i++) {
            final ItemStack item = p.getInventory().getItem(i);

            if (item == null) continue;

            final Material material = item.getType();

            if (!material.isBlock() && material.getMaxDurability() > 0 && item.getDurability() != 0) {
                item.setDurability((short)0);
                repaired.putIfAbsent(item.getType(), 0);

                repaired.put(item.getType(), repaired.get(item.getType()) + item.getAmount());
            }
        }

        if (repaired.isEmpty()) {
            p.sendMessage(ChatUtil.color("&4&lFix &8⏐ &7You do not have any items that can be repaired."));
            return;
        }

        StringBuilder list = new StringBuilder();
        for (int i = 0; i < repaired.size(); i++) {
            if (i != 0) list.append(", ");
            Map.Entry<Material, Integer> entry = (Map.Entry<Material, Integer>) repaired.entrySet().toArray()[i];
            list.append(ChatUtil.clean(entry.getKey().toString())).append(entry.getValue() > 1 ? " x" + entry.getValue() : "");
        }

        p.sendMessage(ChatUtil.color("&4&lFix &8⏐ &7You have successfully repaired your &c" + list.toString() + "&7."));
    }
}
