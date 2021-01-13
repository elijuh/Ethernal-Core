package me.elijuh.core;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.elijuh.core.commands.core.*;
import me.elijuh.core.commands.core.shortcut.Gma;
import me.elijuh.core.commands.core.shortcut.Gmc;
import me.elijuh.core.commands.core.shortcut.Gms;
import me.elijuh.core.commands.core.shortcut.Gmsp;
import me.elijuh.core.commands.economy.BalanceCommand;
import me.elijuh.core.commands.economy.BaltopCommand;
import me.elijuh.core.commands.economy.EconomyCommand;
import me.elijuh.core.commands.economy.PayCommand;
import me.elijuh.core.commands.punishments.*;
import me.elijuh.core.commands.staff.*;
import me.elijuh.core.data.Pair;
import me.elijuh.core.data.User;
import me.elijuh.core.expansions.CoreExpansion;
import me.elijuh.core.hub.HubHandler;
import me.elijuh.core.listeners.ExtraListener;
import me.elijuh.core.listeners.PlayerListener;
import me.elijuh.core.listeners.RedisListener;
import me.elijuh.core.listeners.StaffListener;
import me.elijuh.core.manager.*;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class Core extends JavaPlugin {
    private final List<User> users = new ArrayList<>();
    public static final List<Pair<String, Double>> baltop = new ArrayList<>();
    private static Core instance;
    private CoreExpansion expansion;
    private RedisManager redisManager;
    private DatabaseManager databaseManager;
    private ChatManager chatManager;
    private CoreEconomy economy;
    private Statement keepAlive;
    public static Location spawn;
    public static WarpManager warpManager;
    public static String ID, prefix, logPrefix;

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().addDefault("server-id", "null");
        getConfig().addDefault("ip", "null");
        getConfig().addDefault("discord", "null");
        getConfig().addDefault("economy.enabled", false);
        getConfig().addDefault("economy.starting-balance", 100.0);
        getConfig().addDefault("redis.host", "");
        getConfig().addDefault("redis.port", 6379);
        getConfig().addDefault("redis.password", "");
        getConfig().addDefault("mysql.host", "");
        getConfig().addDefault("mysql.port", 6379);
        getConfig().addDefault("mysql.database", "");
        getConfig().addDefault("mysql.username", "root");
        getConfig().addDefault("mysql.password", "");
        getConfig().addDefault("spawn.world", "world");
        getConfig().addDefault("spawn.x", 0.5);
        getConfig().addDefault("spawn.y", 5.0);
        getConfig().addDefault("spawn.z", 0.5);
        getConfig().addDefault("spawn.yaw", 180.0);
        getConfig().addDefault("spawn.pitch", 0.0);
        saveConfig();

        instance = this;
        expansion = new CoreExpansion();
        redisManager = new RedisManager();
        databaseManager = new DatabaseManager();
        chatManager = new ChatManager();
        try {
            keepAlive = databaseManager.getConnection().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
        spawn = getSpawn();
        warpManager = new WarpManager();
        ID = i().getConfig().getString("server-id");
        if (getConfig().getBoolean("economy.enabled")) {
            economy = new CoreEconomy();
        }
        prefix = ChatUtil.color("&4&lEthernal &8⏐ &7");
        logPrefix = ChatUtil.color("&c&lLog &8⏐ &7");

        if (ID.equals("null")) {
            Bukkit.getLogger().log(Level.SEVERE, "Please set the server-id in config! plugin shutting down.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        new Gma();
        new Gmc();
        new Gms();
        new Gmsp();

        new BanCommand();
        new AltsCommand();
        new KickCommand();
        new MuteCommand();
        new IPBanCommand();
        new UnBanCommand();
        new UnMuteCommand();
        new HistoryCommand();
        new IPUnBanCommand();
        new TempBanCommand();
        new TempMuteCommand();

        new GcCommand();
        new FixCommand();
        new FlyCommand();
        new HubCommand();
        new MsgCommand();
        new FeedCommand();
        new HealCommand();
        new ChatCommand();
        new PingCommand();
        new WarpCommand();
        new ClearCommand();
        new ReplyCommand();
        new SpawnCommand();
        new WarpsCommand();
        new FreezeCommand();
        new InvseeCommand();
        new ReportCommand();
        new SoundsCommand();
        new TphereCommand();
        new VanishCommand();
        new DelwarpCommand();
        new RequestCommand();
        new SetwarpCommand();
        new GamemodeCommand();
        new TeleportCommand();
        new BroadcastCommand();
        new ClearChatCommand();
        new StaffModeCommand();

        if (getConfig().getBoolean("economy.enabled")) {
            new BalanceCommand();
            new EconomyCommand();
            new BaltopCommand();
            new PayCommand();

            Bukkit.getScheduler().runTaskTimerAsynchronously(this, ()-> {
                baltop.clear();
                baltop.addAll(economy.getBaltop());
            }, 0L, 6000L);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, ()-> {
            System.gc();
            try {
                keepAlive.execute("SELECT UUID FROM userdata LIMIT 1");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }, 6000L, 6000L);

        new HubHandler();
        new ExtraListener();
        new RedisListener();
        new StaffListener();
        new PlayerListener();

        for (Player p : Bukkit.getOnlinePlayers()) {
            users.add(new User(p));
            if (ID.toLowerCase().contains("hub")) {
                HubHandler.refresh(p);
            }
            if (getConfig().getBoolean("economy.enabled")) {
                if (!Core.i().getEconomy().hasAccount(p.getName())) {
                    Core.i().getEconomy().createPlayerAccount(p.getName());
                }

                if (p.hasPermission("core.staff")) {
                    StaffManager.enableStaffMode(p);
                    StaffManager.enableVanish(p);
                }
            }
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        PlaceholderAPI.registerExpansion(expansion);
    }

    public void onDisable() {
        for (User user : ImmutableList.copyOf(users)) {
            user.unload();
        }

        users.clear();

        if (PlaceholderAPI.isRegistered("core")) {
            PlaceholderAPI.unregisterExpansion(expansion);
        }

        Bukkit.getServer().getServicesManager().unregisterAll(Core.i());

        instance = null;
        expansion = null;
        if (redisManager != null) {
            redisManager.shutdown();
            redisManager = null;
        }
        if (databaseManager != null) {
            databaseManager.shutdown();
            databaseManager = null;
        }
        chatManager = null;
        try {
            keepAlive.close();
        } catch (NullPointerException | SQLException ignored) {

        }
        keepAlive = null;
        economy = null;
        spawn = null;
        warpManager = null;
        ID = null;
        prefix = null;
        logPrefix = null;
    }

    public static void setSpawn(Location loc) {
        FileConfiguration config = i().getConfig();
        config.set("spawn.world", loc.getWorld().getName());
        config.set("spawn.x", loc.getX());
        config.set("spawn.y", loc.getY());
        config.set("spawn.z", loc.getZ());
        config.set("spawn.yaw", loc.getYaw());
        config.set("spawn.pitch", loc.getPitch());
        i().saveConfig();
        i().reloadConfig();
        spawn = getSpawn();
    }

    public static Location getSpawn() {
        i().reloadConfig();
        FileConfiguration config = i().getConfig();
        return new Location(
                Bukkit.getWorld(config.getString("spawn.world")),
                config.getDouble("spawn.x"),
                config.getDouble("spawn.y"),
                config.getDouble("spawn.z"),
                (float) config.getDouble("spawn.yaw"),
                (float) config.getDouble("spawn.pitch"));
    }

    public User getUser(String name) {
        for (User user : users) {
            if (user.getPlayer().getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    public User getUser(UUID uuid) {
        for (User user : users) {
            if (user.getPlayer().getUniqueId().equals(uuid)) {
                return user;
            }
        }
        return null;
    }

    public User getUser(Player p) {
        if (p == null) {
            return null;
        }
        return getUser(p.getName());
    }

    public static Core i() {
        return instance;
    }
}
