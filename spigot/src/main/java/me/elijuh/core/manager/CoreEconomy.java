package me.elijuh.core.manager;

import com.google.common.collect.ImmutableList;
import me.elijuh.core.Core;
import me.elijuh.core.data.Pair;
import me.elijuh.core.data.YamlFile;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.MathUtil;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CoreEconomy extends AbstractEconomy {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public CoreEconomy() {
        File dataFolder = new File(Core.i().getDataFolder() + File.separator + "userdata");
        if (!dataFolder.getParentFile().exists()) {
            if (dataFolder.getParentFile().mkdir()) {
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aSuccessfully created data folder."));
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&4[ERROR] plugin was not able to create data folder."));
            }
        }
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&aSuccessfully created userdata folder."));
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatUtil.color("&4[ERROR] plugin was not able to create userdata folder."));
            }
        }

        Bukkit.getServer().getServicesManager().register(Economy.class, this, Core.i(), ServicePriority.Normal);
    }

    public List<Pair<String, Double>> getBaltop() {
        List<Pair<String, Double>> baltop = new ArrayList<>();

        for (OfflinePlayer p : getPlayers()) {
            baltop.add(new Pair<>(p.getName(), getBalance(p)));
        }

        baltop.sort(Comparator.comparingDouble(Pair::getY));
        return baltop;
    }

    public List<OfflinePlayer> getPlayers() {
        String[] list = new File(Core.i().getDataFolder() + File.separator + "userdata").list();

        if (list == null) return ImmutableList.of();

        List<OfflinePlayer> players  = new ArrayList<>();

        for (String filename : list) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(filename.split("\\.")[0]));
            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public double getBalance(String name) {
        if (!hasAccount(name)) return 0;

        File file = new File(Core.i().getDataFolder() + File.separator + "userdata" + File.separator
                + Bukkit.getOfflinePlayer(name).getUniqueId().toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getDouble("balance");
    }

    public boolean hasAccount(String name) {
        if (Core.i() == null) return false;

        String[] list = new File(Core.i().getDataFolder() + File.separator + "userdata").list();

        if (list == null) return false;

        for (String filename : list) {
            if (filename.split("\\.")[0].equals(Bukkit.getOfflinePlayer(name).getUniqueId().toString())) {
                YamlFile storage = new YamlFile(filename.substring(0, filename.length() - 4), "userdata");
                return storage.getConfig().contains("balance");
            }
        }
        return false;
    }

    public boolean createPlayerAccount(String name) {
        if (hasAccount(name)) return false;

        File file = new File(Core.i().getDataFolder() + File.separator + "userdata" + File.separator
                + Bukkit.getOfflinePlayer(name).getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);
        config.addDefault("balance", Core.i().getConfig().getDouble("starting-balance"));

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public EconomyResponse withdrawPlayer(String name, double amount) {
        amount = MathUtil.roundTo(amount, 2);
        if (!hasAccount(name)) {
            return new EconomyResponse(0.0, 0.0,
                    EconomyResponse.ResponseType.FAILURE, "The player does not have an account!");
        }
        double balance = getBalance(name) - amount;
        if (!has(name, amount)) {
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "The value is more than the player's balance!");
        }
        File file = new File(Core.i().getDataFolder() + File.separator + "userdata" + File.separator
                + Bukkit.getOfflinePlayer(name).getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("balance", balance);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(String name, double amount) {
        amount = MathUtil.roundTo(amount, 2);
        if (!hasAccount(name)) {
            return new EconomyResponse(0.0, 0.0,
                    EconomyResponse.ResponseType.FAILURE, "The player does not have an account!");
        }
        if (amount < 0.01) {
            return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "The amount is too small!");
        }
        double balance = getBalance(name) + amount;
        File file = new File(Core.i().getDataFolder() + File.separator + "userdata" + File.separator
                + Bukkit.getOfflinePlayer(name).getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("balance", balance);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public boolean isEnabled() {
        return Core.i() != null;
    }

    public String getName() {
        return "Core";
    }

    public boolean hasBankSupport() {
        return false;
    }

    public int fractionalDigits() {
        return -1;
    }

    public String format(double amount) {
        return df.format(amount);
    }

    public String currencyNamePlural() {
        return "";
    }

    public String currencyNameSingular() {
        return "";
    }

    public boolean hasAccount(String name, String world) {
        return hasAccount(name);
    }

    public double getBalance(String name, String world) {
        return getBalance(name);
    }

    public boolean has(String name, double amount) {
        return getBalance(name) >= amount;
    }

    public boolean has(String name, String world, double amount) {
        return has(name, amount);
    }

    public EconomyResponse withdrawPlayer(String name, String world, double amount) {
        return withdrawPlayer(name, amount);
    }

    public EconomyResponse depositPlayer(String name, String world, double amount) {
        return depositPlayer(name, amount);
    }

    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    public EconomyResponse deleteBank(String s) {
        return null;
    }

    public EconomyResponse bankBalance(String s) {
        return null;
    }

    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    public List<String> getBanks() {
        return null;
    }

    public boolean createPlayerAccount(String name, String world) {
        return createPlayerAccount(name);
    }
}
