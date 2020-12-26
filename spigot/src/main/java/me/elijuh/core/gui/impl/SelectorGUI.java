package me.elijuh.core.gui.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import me.elijuh.core.gui.GUI;
import me.elijuh.core.utils.BungeeUtil;
import me.elijuh.core.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class SelectorGUI extends GUI {
    private static ItemStack KITPVP;
    private static ItemStack SURVIVAL;

    public SelectorGUI() {
        super("&4&lServer Selector", 3);
    }

    @Override
    public void setItems(Player player) {
        for (int i = 0; i < getRows() * 9; i++) {
            getInv().setItem(i, GUI.FILLER);
        }

        KITPVP = new ItemBuilder(Material.DIAMOND_SWORD)
                .setName("&8&l« &4&lKitPvP &8&l»")
                .addLore(" ")
                .addLore("&c&lVersion &8» &f1.8.9")
                .addLore("&c&lStatus &8» &fMaintenance")
                .addLore(" ")
                .addLore("&c&lPlayers &8» &f" + PlaceholderAPI.setPlaceholders(player, "%bungee_kitpvp%"))
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build();

        SURVIVAL = new ItemBuilder(Material.GRASS)
                .setName("&8&l« &4&lSurvival &8&l»")
                .addLore(" ")
                .addLore("&c&lVersion &8» &f1.16.4")
                .addLore("&c&lStatus &8» &fMaintenance")
                .addLore(" ")
                .addLore("&c&lPlayers &8» &f" + PlaceholderAPI.setPlaceholders(player, "%bungee_survival%"))
                .build();

        getInv().setItem(12, KITPVP);
        getInv().setItem(14, SURVIVAL);
    }

    @Override
    public void open(Player player) {
        setItems(player);
        player.openInventory(getInv());
    }

    @Override
    public void handle(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(getTitle())) {
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().isSimilar(KITPVP)) {
                BungeeUtil.send((Player) e.getWhoClicked(), "kitpvp");
            } else if (e.getCurrentItem().isSimilar(SURVIVAL)) {
                BungeeUtil.send((Player) e.getWhoClicked(), "survival");
            }
            e.setCancelled(true);
        }
    }
}
