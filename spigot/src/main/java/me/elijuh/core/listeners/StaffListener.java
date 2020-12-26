package me.elijuh.core.listeners;

import me.elijuh.core.Core;
import me.elijuh.core.gui.impl.StaffGUI;
import me.elijuh.core.manager.StaffManager;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.StaffUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class StaffListener implements Listener {
    public static StaffGUI staffGui = new StaffGUI();

    public StaffListener(Core plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(PlayerInteractEvent e) {
        if (e.getPlayer().getItemInHand() != null) {
            if (StaffManager.isStaffItem(e.getPlayer().getItemInHand()))
                e.setCancelled(true);
        }
        if (e.getAction().toString().contains("RIGHT") && StaffUtil.isStaffMode(e.getPlayer())) {
            Player p = e.getPlayer();
            ItemStack item = p.getItemInHand();
            if (item == null) return;
            if (item.isSimilar(StaffManager.VANISH) || item.isSimilar(StaffManager.UN_VANISH)) {
                p.performCommand("vanish");
            } else if (item.isSimilar(StaffManager.STAFF)) {
                staffGui.open(p);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent e) {
        if (!e.getRightClicked().getType().equals(EntityType.PLAYER) || !StaffUtil.isStaffMode(e.getPlayer())) return;
        Player p = e.getPlayer();
        ItemStack item = p.getItemInHand();
        Player target = (Player) e.getRightClicked();
        if (item.isSimilar(StaffManager.INSPECT)) {
            p.performCommand("invsee " + target.getName());
        } else if (item.isSimilar(StaffManager.FREEZE)) {
            p.performCommand("freeze " + target.getName());
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getDamager();
            if (StaffUtil.isVanished(p)) {
                p.sendMessage(ChatUtil.color("&6» &eYou cannot hit whilst vanished."));
                e.setCancelled(true);
            } else if (StaffUtil.isFrozen(p)) {
                p.sendMessage(ChatUtil.color("&6» &eYou cannot hit whilst frozen."));
                e.setCancelled(true);
            } else if (e.getEntity().getType().equals(EntityType.PLAYER)) {
                Player target = (Player) e.getEntity();
                if (StaffUtil.isFrozen(target)) {
                    p.sendMessage(ChatUtil.color("&7" + target.getName() + " &cis currently frozen and cannot be hit."));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (StaffUtil.isStaffMode((Player)e.getWhoClicked())) {
            if (!e.getWhoClicked().hasPermission("core.admin")) {
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
            } else {
                if (e.getCurrentItem() == null) return;
                if (StaffManager.isStaffItem(e.getCurrentItem())) {
                    e.setCancelled(true);
                    e.getView().close();
                }
            }
        }
        if (StaffUtil.isFrozen((Player)e.getWhoClicked())) {
            e.setCancelled(true);
            e.getView().close();
        }

        if (e.getView().getTitle().equals(staffGui.getTitle())) {
            staffGui.handle(e);
        }
    }

    @EventHandler
    public void on(BlockPlaceEvent e) {
        if (e.getItemInHand().isSimilar(StaffManager.FREEZE) || (StaffUtil.isStaffMode(e.getPlayer()) && !e.getPlayer().hasPermission("core.staffmode.interact"))) {
            e.setCancelled(true);
        }

        if (StaffUtil.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        if (StaffUtil.isStaffMode(e.getPlayer()) && !e.getPlayer().hasPermission("core.staffmode.interact")) {
            e.setCancelled(true);
        }

        if (StaffUtil.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        if (StaffUtil.isVanished(e.getPlayer()) || StaffUtil.isStaffMode(e.getPlayer())) {
            e.setCancelled(true);
        }

        if (StaffUtil.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerPickupItemEvent e) {
        if (StaffUtil.isVanished(e.getPlayer()) || StaffUtil.isStaffMode(e.getPlayer())) {
            e.setCancelled(true);
        }

        if (StaffUtil.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getEntity();
            if (StaffUtil.isVanished(p) || StaffUtil.isStaffMode(p) || StaffUtil.isFrozen(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent e) {
        Location to = e.getTo();
        Location from = e.getFrom();
        if (to.getX() == from.getX()
            && to.getZ() == from.getZ()) return;
        Player p = e.getPlayer();
        if (StaffUtil.isFrozen(p)) {
            p.teleport(from);
        }
    }
}
