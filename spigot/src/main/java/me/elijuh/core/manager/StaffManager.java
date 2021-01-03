package me.elijuh.core.manager;

import me.elijuh.core.Core;
import me.elijuh.core.utils.NametagUtil;
import me.elijuh.core.utils.ItemBuilder;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import me.elijuh.core.tasks.FrozenTask;

import java.util.HashMap;
import java.util.Map;

public class StaffManager {
    public static final Map<String, ItemStack[]> contents = new HashMap<>();
    public static final Map<String, ItemStack[]> armor = new HashMap<>();

    public static final ItemStack COMPASS = new ItemBuilder(Material.COMPASS).setName("&4Compass")
            .addLore("&7Launch Compass.").build();
    public static final ItemStack INSPECT = new ItemBuilder(Material.BOOK).setName("&4Inspect Player")
            .addLore("&7Right-Click a player to inspect their inventory.").build();
    public static final ItemStack FREEZE = new ItemBuilder(Material.ICE).setName("&4Freeze Player")
            .addLore("&7Right-Click a player to freeze them.").build();
    public static final ItemStack CARPET = new ItemBuilder(Material.CARPET).setName("&4Better Looking")
            .addLore("&7Hold this so you don't see your hand.").setDura(1).build();
    public static final ItemStack STAFF = new ItemBuilder(Material.NETHER_STAR).setName("&4Online Staff")
            .addLore("&7Right-Click for a GUI of online staff.").build();
    public static final ItemStack VANISH = new ItemBuilder(Material.INK_SACK).setName("&4Become Invisible")
            .addLore("&7Right-Click to toggle vanish.").setDura(10).build();
    public static final ItemStack UN_VANISH = new ItemBuilder(Material.INK_SACK).setName("&4Become Visible")
            .addLore("&7Right-Click to toggle vanish.").setDura(8).build();

    public static boolean isStaffItem(ItemStack item) {
        return item.isSimilar(COMPASS)
                || item.isSimilar(INSPECT)
                || item.isSimilar(FREEZE)
                || item.isSimilar(CARPET)
                || item.isSimilar(STAFF)
                || item.isSimilar(VANISH)
                || item.isSimilar(UN_VANISH);
    }

    public static void toggleStaffMode(Player player) {
        if (Core.ID.toLowerCase().contains("hub")) return;
        if (StaffUtil.isStaffMode(player)) {
            disableStaffMode(player);
        } else {
            enableStaffMode(player);
        }
    }

    public static void enableStaffMode(Player player) {
        if (Core.ID.toLowerCase().contains("hub")) return;
        player.setMetadata("staffmode", new FixedMetadataValue(Core.i(), true));
        PlayerInventory inv = player.getInventory();
        contents.put(player.getName(), inv.getContents());
        armor.put(player.getName(), inv.getArmorContents());
        inv.clear();
        inv.setArmorContents(null);
        inv.setItem(0, COMPASS);
        inv.setItem(1, INSPECT);
        inv.setItem(2, player.hasPermission("core.freeze") ? FREEZE : CARPET);
        inv.setItem(3, player.hasPermission("core.freeze") ? CARPET : null);
        inv.setItem(7, STAFF);
        inv.setItem(8, StaffUtil.isVanished(player) ? UN_VANISH : VANISH);
        player.setGameMode(GameMode.CREATIVE);
    }

    public static void disableStaffMode(Player player) {
        if (Core.ID.toLowerCase().contains("hub")) return;
        player.removeMetadata("staffmode", Core.i());
        if (contents.containsKey(player.getName())) {
            player.getInventory().setContents(contents.get(player.getName()));
        }

        if (armor.containsKey(player.getName())) {
            player.getInventory().setArmorContents(armor.get(player.getName()));
        }

        player.setGameMode(GameMode.SURVIVAL);
    }

    public static void toggleVanish(Player player) {
        if (Core.ID.toLowerCase().contains("hub")) return;
        if (StaffUtil.isVanished(player)) {
            disableVanish(player);
        } else {
            enableVanish(player);
        }
    }

    public static void enableVanish(Player player) {
        if (Core.ID.toLowerCase().contains("hub")) return;
        player.setMetadata("vanished", new FixedMetadataValue(Core.i(), true));
        NametagUtil.setPrefix(player, "&7[V] ");
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!all.hasPermission("core.staff")) {
                all.hidePlayer(player);
            }
        }
        if (StaffUtil.isStaffMode(player)) {
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) continue;
                if (item.isSimilar(VANISH) || item.isSimilar(UN_VANISH)) {
                    player.getInventory().setItem(i, StaffUtil.isVanished(player) ? UN_VANISH : VANISH);
                }
            }
        }
    }

    public static void enableVanish(Player player, long delayNametag) {
        if (Core.ID.toLowerCase().contains("hub")) return;
        player.setMetadata("vanished", new FixedMetadataValue(Core.i(), true));
        Bukkit.getScheduler().runTaskLater(Core.i(), ()-> NametagUtil.setPrefix(player, "&7[V] "), delayNametag);
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!all.hasPermission("core.staff")) {
                all.hidePlayer(player);
            }
        }
        if (StaffUtil.isStaffMode(player)) {
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) continue;
                if (item.isSimilar(VANISH) || item.isSimilar(UN_VANISH)) {
                    player.getInventory().setItem(i, StaffUtil.isVanished(player) ? UN_VANISH : VANISH);
                }
            }
        }
    }

    public static void disableVanish(Player player) {
        if (Core.ID.toLowerCase().contains("hub")) return;
        player.removeMetadata("vanished", Core.i());
        NametagUtil.removePrefix(player);
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!all.canSee(player)) {
                all.showPlayer(player);
            }
        }
        if (StaffUtil.isStaffMode(player)) {
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) continue;
                if (item.isSimilar(VANISH) || item.isSimilar(UN_VANISH)) {
                    player.getInventory().setItem(i, StaffUtil.isVanished(player) ? UN_VANISH : VANISH);
                }
            }
        }
    }

    public static void unfreeze(Player target) {
        if (StaffUtil.isFrozen(target)) {
            target.removeMetadata("frozen", Core.i());
        }
    }

    public static void freeze(Player target) {
        if (!StaffUtil.isFrozen(target)) {
            target.setMetadata("frozen", new FixedMetadataValue(Core.i(), true));
            new FrozenTask(target);
        }
    }
}
