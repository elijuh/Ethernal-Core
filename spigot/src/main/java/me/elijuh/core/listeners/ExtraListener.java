package me.elijuh.core.listeners;

import me.elijuh.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ExtraListener implements Listener {

    public ExtraListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Core.i());
    }

    @EventHandler
    public void on(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }
}
