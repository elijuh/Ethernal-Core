package me.elijuh.core.gui;

import lombok.Data;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Data
public abstract class GUI {
    public static final ItemStack FILLER = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDura(15).build();
    private Inventory inv;
    private String title;
    private int rows;

    public GUI(String title, int rows) {
        this.title = ChatUtil.color(title);
        this.rows = rows;
        inv = Bukkit.createInventory(null, this.rows * 9, this.title);
    }

    public abstract void setItems(Player player);

    public abstract void open(Player player);

    public abstract void handle(InventoryClickEvent e);
}
