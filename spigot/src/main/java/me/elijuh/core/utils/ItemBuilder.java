package me.elijuh.core.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setType(Material material) {
        this.item.setType(material);
        return this;
    }

    public ItemBuilder setDura(int dura) {
        this.item.setDurability((short) dura);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(Math.min(amount, 64));
        return this;
    }

    public ItemBuilder setName(String name) {
        this.meta.setDisplayName(ChatUtil.color(name));
        return this;
    }

    public ItemBuilder addLore(String line) {
        List<String> lore = this.meta.hasLore() ? this.meta.getLore() : new ArrayList<>();
        lore.add(ChatUtil.color(line));
        this.meta.setLore(lore);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return item;
    }
}
