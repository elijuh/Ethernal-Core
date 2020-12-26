package me.elijuh.core;

import me.elijuh.core.staff.ServerSwitch;
import me.elijuh.core.staff.StaffChat;
import net.md_5.bungee.api.plugin.Plugin;

public class Core extends Plugin {
    private static Core instance;

    public void onEnable() {
        instance = this;
        new StaffChat();
        new ServerSwitch();
    }

    public void onDisable() {
        instance = null;
    }

    public static Core getInstance() {
        return instance;
    }
}
