package me.elijuh.core.gui.impl;

import me.elijuh.core.gui.GUI;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.PlayerUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class StaffGUI extends GUI {
    public StaffGUI() {
        super("&4Staff", 1);
    }

    @Override
    public void setItems(Player player) {
        setInv(Bukkit.createInventory(null, (((int) Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("core.staff")).count() - 1) / 9 + 1) * 9, getTitle()));
        int index = 0;
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("core.staff")) {
                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setDisplayName(PlayerUtil.getPrefix(staff) + staff.getName());
                meta.setOwner(staff.getName());
                List<String> lore = new ArrayList<>();
                lore.add(" ");
                lore.add(ChatUtil.color("&4&lStaff Information &8»"));
                lore.add(ChatUtil.color("&8⏐ &7Vanish: " + (StaffUtil.isVanished(staff) ? "&aEnabled" : "&cDisabled")));
                lore.add(ChatUtil.color("&8⏐ &7Staff Mode: " + (StaffUtil.isStaffMode(staff) ? "&aEnabled" : "&cDisabled")));
                lore.add(" ");
                lore.add(ChatUtil.color("&f&nClick to teleport."));

                meta.setLore(lore);
                item.setItemMeta(meta);
                getInv().setItem(index, item);
                index++;
            }
        }
    }

    @Override
    public void open(Player player) {
        setItems(player);
        player.openInventory(getInv());
    }

    @Override
    public void handle(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        if (item.getType().equals(Material.SKULL_ITEM)) {
            Player target = Bukkit.getPlayerExact(((SkullMeta) item.getItemMeta()).getOwner());
            if (target == null) {
                e.getWhoClicked().sendMessage(ChatUtil.color("&cThat player is no longer online."));
            } else {
                ((Player)e.getWhoClicked()).performCommand("tp " + target.getName());
            }
        }
        e.setCancelled(true);
        e.getView().close();
    }
}
