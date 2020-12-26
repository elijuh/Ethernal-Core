package me.elijuh.core.data;

import lombok.Getter;
import me.elijuh.core.Core;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class YamlFile {
    private static final Core plugin = Core.i();
    private final File file;
    private YamlConfiguration config;

    public YamlFile(String filename, String path) {
        file = new File(plugin.getDataFolder() + File.separator + (path.isEmpty() ? "" : path + File.separator) + filename + ".yml");
        if (!file.getParentFile().exists()) {
            if (file.getParentFile().mkdir()) {
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aSuccessfully created " + file.getParentFile().getName() + " folder."));
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&4[ERROR] plugin was not able to create " + file.getParentFile().getName() + " folder."));
            }
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aCreated new YamlFile: &6" + filename + ".yml"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reload();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
