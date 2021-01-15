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

import java.util.UUID;

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
                handlePunishment(manager.getGSON().fromJson(json, PunishmentInfo.class));
                break;
            }
        }
    }

    private void handlePunishment(PunishmentInfo punishment) {
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
                        user.sendMessage(punishment.getExecutorDisplay() + " &7kicked " +
                                punishment.getPunishedDisplay());
                        user.sendMessage("&7Reason: &f" + punishment.getReason());
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
                            user.sendMessage(punishment.getExecutorDisplay() + " &7unbanned " +
                                    punishment.getPunishedDisplay());
                            user.sendMessage("&7Reason: &f" + punishment.getReason());
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
                            user.sendMessage(punishment.getExecutorDisplay() + (perm ? " &7banned " : " &7temporarily banned ") +
                                    punishment.getPunishedDisplay());
                            user.sendMessage("&7Reason: &f" + punishment.getReason());
                            user.sendMessage("&7Duration: &f" + (perm ? "Permanent" : ChatUtil.formatMillis(punishment.getLength())));
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
                            user.sendMessage(punishment.getExecutorDisplay() + " &7unblacklisted " +
                                    punishment.getPunishedDisplay());
                            user.sendMessage("&7Reason: &f" + punishment.getReason());
                            user.sendMessage("&8&m------------------------------------------");
                        }
                    }
                } else {
                    String punished = punishment.getPunished();
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
                            user.sendMessage(punishment.getExecutorDisplay() +  " &7blacklisted " +
                                    punishment.getPunishedDisplay());
                            user.sendMessage("&7Reason: &f" + punishment.getReason());
                            user.sendMessage("&7Duration: &fPermanent");
                            user.sendMessage("&8&m------------------------------------------");
                        }
                    }
                }
                break;
            }
            case MUTE: {
                User u = Core.i().getUser(punishment.getPunished());
                if (punishment.isRemoval()) {
                    if (u != null) {
                        u.sendMessage(ChatUtil.color("&aYou have been unmuted."));
                    }

                    for (User user : Core.i().getUsers()) {
                        if (user.isStaff()) {
                            user.sendMessage("&8&m------------------------------------------");
                            user.sendMessage(punishment.getExecutorDisplay() + " &7unmuted " +
                                    punishment.getPunishedDisplay());
                            user.sendMessage("&7Reason: &f" + punishment.getReason());
                            user.sendMessage("&8&m------------------------------------------");
                        }
                    }
                } else {
                    boolean perm = punishment.getLength() == -1;
                    if (u != null) {
                        u.sendMessage(" ");
                        u.sendMessage("&cYou have been muted! &8(&7Reason: &f" + punishment.getReason() + " &8| &7Duration: &f" + (perm ? "Permanent" : ChatUtil.formatMillis(punishment.getLength())) + "&8)");
                        u.sendMessage(" ");
                    }

                    for (User user : Core.i().getUsers()) {
                        if (user.isStaff()) {
                            user.sendMessage("&8&m------------------------------------------");
                            user.sendMessage(punishment.getExecutorDisplay() + (perm ? " &7muted " : " &7temporarily muted ") +
                                    punishment.getPunishedDisplay());
                            user.sendMessage("&7Reason: &f" + punishment.getReason());
                            user.sendMessage("&7Duration: &f" + (perm ? "Permanent" : ChatUtil.formatMillis(punishment.getLength())));
                            user.sendMessage("&8&m------------------------------------------");
                        }
                    }
                }
                break;
            }
        }
    }
}
