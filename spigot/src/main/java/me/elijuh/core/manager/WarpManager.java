package me.elijuh.core.manager;

import lombok.Getter;
import me.elijuh.core.Core;
import me.elijuh.core.data.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WarpManager {
    private File file;
    @Getter private FileConfiguration config;

    public WarpManager() {
        file = new File(String.valueOf(Core.i().getDataFolder()));
        file.mkdir();
        file = new File(Core.i().getDataFolder() + File.separator + "warps.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        reloadConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save warps config.");
        }
    }

    public List<Warp> getWarps() {
        List<Warp> warps = new ArrayList<>();
        if (config.getConfigurationSection("warps") == null) {
            return warps;
        }

        for (String key : config.getConfigurationSection("warps").getKeys(false)) {
            World world = Bukkit.getWorld(config.getString("warps." + key + "." + "world"));
            double x = config.getDouble("warps." + key + "." + "x");
            double y = config.getDouble("warps." + key + "." + "y");
            double z = config.getDouble("warps." + key + "." + "z");
            float yaw = (float) config.getDouble("warps." + key + "." + "yaw");
            float pitch = (float) config.getDouble("warps." + key + "." + "pitch");
            String permission = config.getString("warps." + key + "." + "permission");
            try {
                warps.add(new Warp(key, new Location(world, x, y, z, yaw, pitch), permission));
            } catch(Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to load warp: " + key);
            }
        }
        return warps;
    }

    public void createWarp(Warp warp) {
        Location loc = warp.getLocation();
        config.set("warps." + warp.getName() + "." + "world", loc.getWorld().getName());
        config.set("warps." + warp.getName() + "." + "x", loc.getX());
        config.set("warps." + warp.getName() + "." + "y", loc.getY());
        config.set("warps." + warp.getName() + "." + "z", loc.getZ());
        config.set("warps." + warp.getName() + "." + "yaw", loc.getYaw());
        config.set("warps." + warp.getName() + "." + "pitch", loc.getPitch());
        config.set("warps." + warp.getName() + "." + "permission", warp.getPermission());
        saveConfig();
    }

    public void deleteWarp(Warp warp) {
        config.set("warps." + warp.getName(), null);
        saveConfig();
    }

    public Warp getWarp(String name) {
        for (Warp warp : getWarps()) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }
}
