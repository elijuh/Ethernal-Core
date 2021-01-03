package me.elijuh.core.hub;

import me.elijuh.core.Core;
import me.elijuh.core.gui.impl.SelectorGUI;
import me.elijuh.core.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class HubHandler implements Listener {

    private static final Map<String, SelectorGUI> selectorInstances = new HashMap<>();
    private static final Map<String, HubScoreboard> scoreboardInstances = new HashMap<>();

    public static final ItemStack SELECTOR = new ItemBuilder(Material.NETHER_STAR).setName("&c&lSelector").build();
    public static final ItemStack PEARL = new ItemBuilder(Material.ENDER_PEARL).setName("&c&lPearl").setAmount(1).build();

    public HubHandler() {
        if (Core.ID.toLowerCase().contains("hub")) {
            Bukkit.getServer().getPluginManager().registerEvents(this, Core.i());
        }
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item.isSimilar(SELECTOR) || item.isSimilar(PEARL)) {
            e.setCancelled(true);
            e.getView().close();
        }

        SelectorGUI instance = selectorInstances.computeIfAbsent(p.getName(), name -> new SelectorGUI());
        if (e.getView().getTitle().equals(instance.getTitle())) {
            instance.handle(e);
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent e) {
        if (e.getAction().toString().contains("RIGHT")) {
            if (e.getPlayer().getItemInHand().isSimilar(PEARL)) {
                e.setCancelled(true);
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(2.5).setY(1));
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ORB_PICKUP, 1.0F, 2.0F);
                e.getPlayer().updateInventory();
            } else if (e.getPlayer().getItemInHand().isSimilar(SELECTOR)) {
                selectorInstances.putIfAbsent(e.getPlayer().getName(), new SelectorGUI());
                selectorInstances.get(e.getPlayer().getName()).open(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        e.getPlayer().teleport(Core.spawn);
        refresh(e.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        if (scoreboardInstances.containsKey(e.getPlayer().getName())) {
            scoreboardInstances.get(e.getPlayer().getName()).disable();
        }

        selectorInstances.remove(e.getPlayer().getName());
        scoreboardInstances.remove(e.getPlayer().getName());
    }

    public static void refresh(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setWalkSpeed(0.4F);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(3, SELECTOR);
        player.getInventory().setItem(5, PEARL);
        player.getInventory().setHeldItemSlot(3);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        scoreboardInstances.computeIfAbsent(player.getName(), name -> new HubScoreboard(player)).enable();
    }
}
