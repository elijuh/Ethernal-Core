package me.elijuh.core.listeners;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import me.elijuh.core.Core;
import me.elijuh.core.data.User;
import me.elijuh.core.data.YamlFile;
import me.elijuh.core.data.redis.PunishmentInfo;
import me.elijuh.core.data.redis.ReportInfo;
import me.elijuh.core.data.redis.RequestInfo;
import me.elijuh.core.data.redis.UserDataUpdateInfo;
import me.elijuh.core.manager.DatabaseManager;
import me.elijuh.core.manager.RedisManager;
import me.elijuh.core.utils.ChatUtil;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.logging.Level;

public class RedisListener extends RedisPubSubAdapter<String, String> {
    RedisManager manager;
    DatabaseManager databaseManager;

    public RedisListener() {
        manager = Core.i().getRedisManager();
        databaseManager = Core.i().getDatabaseManager();
        manager.getPubSubSubscriber().addListener(this);
    }

    @Override
    public void message(String channel, String json) {
        switch (channel) {
            case "REPORT": {
                ReportInfo report = manager.getGSON().fromJson(json, ReportInfo.class);
                for (User user : Core.i().getUsers()) {
                    if (user.isStaff()) {
                        user.sendMessage("&8[&4&lReport&8] &7[" + report.getServer() + "&7] &f" + report.getReported() + " &7was reported by &f" + report.getReporter());
                        user.sendMessage("&8[&4&l!&8] &cReason: &7" + report.getReason());
                    }
                }
                break;
            }
            case "REQUEST": {
                RequestInfo request = manager.getGSON().fromJson(json, RequestInfo.class);
                for (User user : Core.i().getUsers()) {
                    if (user.isStaff()) {
                        user.sendMessage("&8[&4&lRequest&8] &7[" + request.getServer() + "&7] &f" + request.getRequester() + " &7has requested assistance.");
                        user.sendMessage("&8[&4&l!&8] &cReason: &7" + request.getRequest());
                    }
                }
                break;
            }
            case "USERDATA": {
                UserDataUpdateInfo update = manager.getGSON().fromJson(json, UserDataUpdateInfo.class);
                YamlFile storage = new YamlFile(update.getUuid(), "userdata");
                storage.getConfig().set(update.getPath(), update.getValue());
                storage.save();
                User user = Core.i().getUser(UUID.fromString(update.getUuid()));
                if (user != null) {
                    user.getUserData().getStorage().reload();
                }
                break;
            }
            case "PUNISHMENT": {
                PunishmentInfo punishment = manager.getGSON().fromJson(json, PunishmentInfo.class);
                switch (punishment.getType()) {
                    case KICK: {
                        if (Core.i().getUser(punishment.getPunished()) != null) {
                            Core.i().getUser(punishment.getPunished()).kick(
                                    "&4&lEthernal &8⏐ &fKicked",
                                " ",
                                "&7Reason: &f" + punishment.getReason());
                        }

                        for (User user : Core.i().getUsers()) {
                            if (user.isStaff()) {
                                user.sendMessage("&8&m------------------------------------------");
                                user.sendMessage("&4&lStaff &8⏐ &r" + punishment.getExecutorDisplay() + " &akicked " +
                                        punishment.getPunishedDisplay());
                                user.sendMessage("&cReason: &7" + punishment.getReason());
                                user.sendMessage("&8&m------------------------------------------");
                            }
                        }
                        break;
                    }
                    case BAN: {
                        if (punishment.isRemoval()) {
                            for (User user : Core.i().getUsers()) {
                                if (user.isStaff()) {
                                    user.sendMessage("&8&m------------------------------------------");
                                    user.sendMessage("&4&lStaff &8⏐ &r" + punishment.getExecutorDisplay() + " &aunbanned " +
                                            punishment.getPunishedDisplay());
                                    user.sendMessage("&cReason: &7" + punishment.getReason());
                                    user.sendMessage("&8&m------------------------------------------");
                                }
                            }
                        } else {
                            boolean perm = punishment.getLength() == -1;
                            if (Core.i().getUser(punishment.getPunished()) != null) {
                                Core.i().getUser(punishment.getPunished()).kick(
                                        "&4&lEthernal &8⏐ &fYou are Banned!",
                                        " ",
                                        "&7Reason: &f" + punishment.getReason(),
                                        "&7Duration: &f" + (perm ? "Permanent" : ChatUtil.formatMillis(punishment.getLength())));
                            }

                            for (User user : Core.i().getUsers()) {
                                if (user.isStaff()) {
                                    user.sendMessage("&8&m------------------------------------------");
                                    user.sendMessage("&4&lStaff &8⏐ &r" + punishment.getExecutorDisplay() + (perm ? " &abanned " : " &atempbanned ") +
                                            punishment.getPunishedDisplay());
                                    user.sendMessage("&cReason: &7" + punishment.getReason());
                                    user.sendMessage("&cDuration: &7" + (perm ? "Permanent" : ChatUtil.formatMillis(punishment.getLength())));
                                    user.sendMessage("&8&m------------------------------------------");
                                }
                            }
                        }
                        break;
                    }
                    case IPBAN: {
                        if (punishment.isRemoval()) {
                            for (User user : Core.i().getUsers()) {
                                if (user.isStaff()) {
                                    user.sendMessage("&8&m------------------------------------------");
                                    user.sendMessage("&4&lStaff &8⏐ &r" + punishment.getExecutorDisplay() + " &aunblacklisted " +
                                            punishment.getPunishedDisplay());
                                    user.sendMessage("&cReason: &7" + punishment.getReason());
                                    user.sendMessage("&8&m------------------------------------------");
                                }
                            }
                        } else {
                            String punished = punishment.getPunished();
                            if (databaseManager == null) {
                                Bukkit.getLogger().log(Level.SEVERE, "database manager is somehow null");
                                return;
                            } else if (punished == null) {
                                Bukkit.getLogger().log(Level.SEVERE, "punished is somehow null");
                                return;
                            }
                            String uuid = databaseManager.getUUID(punished);
                            String ip = databaseManager.getIP(uuid);

                            for (User user : Core.i().getUsers()) {
                                if (databaseManager.getIP(user.getPlayer().getUniqueId().toString()).equals(ip)) {
                                    user.kick(
                                            "&4&lEthernal &8⏐ &fYou are IPBanned!",
                                            " ",
                                            "&7Reason: &f" + punishment.getReason(),
                                            "&7Duration: &fPermanent");
                                }
                            }

                            for (User user : Core.i().getUsers()) {
                                if (user.isStaff()) {
                                    user.sendMessage("&8&m------------------------------------------");
                                    user.sendMessage("&4&lStaff &8⏐ &r" + punishment.getExecutorDisplay() +  " &ablacklisted " +
                                            punishment.getPunishedDisplay());
                                    user.sendMessage("&cReason: &7" + punishment.getReason());
                                    user.sendMessage("&cDuration: &7Permanent");
                                    user.sendMessage("&8&m------------------------------------------");
                                }
                            }
                        }
                        break;

                        //also add a clear history and view history
                    }
                }
            }
        }
    }
}
