package me.elijuh.core.manager;

import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.elijuh.core.Core;
import me.elijuh.core.data.BanInfo;
import me.elijuh.core.data.Punishment;
import me.elijuh.core.data.User;
import me.elijuh.core.data.redis.PunishmentInfo;
import me.elijuh.core.utils.ChatUtil;
import me.elijuh.core.utils.PlayerUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.List;
import java.util.UUID;

@Getter
public class DatabaseManager {
    private final HikariDataSource hikariDataSource;
    private final Connection connection;
    private final RedisManager redisManager;

    public DatabaseManager() {
        redisManager = Core.i().getRedisManager();

        FileConfiguration config = Core.i().getConfig();
        String host = config.getString("mysql.host");
        int port = config.getInt("mysql.port");
        String database = config.getString("mysql.database");
        String username = config.getString("mysql.username");
        String password = config.getString("mysql.password");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName("Core");
        hikariConfig.setMaximumPoolSize(6);
        hikariConfig.setAutoCommit(true);

        hikariDataSource = new HikariDataSource(hikariConfig);

        Connection c;
        try {
            c = hikariDataSource.getConnection();
        } catch (SQLException e) {
            c = null;
        }

        this.connection = c;

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS punishments(`UUID` VARCHAR(36), `IP` VARCHAR(20), " +
                    "`type` VARCHAR(10), `removed` VARCHAR(16) DEFAULT 0, `expiration` BIGINT(16), `reason` VARCHAR(255), `executor` VARCHAR(16))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS userdata(`UUID` VARCHAR(36) PRIMARY KEY, `IP` VARCHAR(20), `name` VARCHAR(16), `display` VARCHAR(32))");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            if (hikariDataSource.getConnection() != null) {
                return !hikariDataSource.getConnection().isClosed();
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void shutdown() {
        if (isConnected()) {
            try {
                if (hikariDataSource.getConnection() != null) {
                    hikariDataSource.getConnection().close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }

    public boolean isIPBanned(String ip) {
        if (ip.equals("Unknown")) {
            return false;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT UUID FROM punishments WHERE type = \"IPBAN\" AND IP = ? AND removed = \"0\"");
            statement.setString(1, ip);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPunished(String name, Punishment type) {
        if (hasData(name)) {
            return isPunished(UUID.fromString(getUUID(name)), type);
        }
        return false;
    }

    public boolean isPunished(UUID uuid, Punishment type) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT expiration FROM punishments WHERE UUID = ? AND type = ? AND removed = \"0\"");
            statement.setString(1, uuid.toString());
            statement.setString(2, type.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (result.getString("expiration").equals("-1")) {
                    return true;
                } else if (result.getLong("expiration") - System.currentTimeMillis() > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void punish(PunishmentInfo info) {
        redisManager.getPubSubSender().async().publish("PUNISHMENT", redisManager.getGSON().toJson(info));

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO punishments(`UUID`, `IP`, `type`, `expiration`, " +
                    "`reason`, `executor`) VALUES(?, ?, ?, ?, ?, ?)");
            statement.setString(1, getUUID(info.getPunished()));
            statement.setString(2, getIP(getUUID(info.getPunished())));
            statement.setString(3, info.getType().toString());
            statement.setString(4, String.valueOf(info.getLength() == -1 ? -1 : info.getExpiration()));
            statement.setString(5, info.getReason());
            statement.setString(6, info.getExecutor());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(String uuid, PunishmentInfo info) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE punishments SET `removed` = ? WHERE `UUID` = ? AND `type` = ? AND removed = \"0\"");
            statement.setString(1, info.getExecutor());
            statement.setString(2, uuid);
            statement.setString(3, info.getType().toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        redisManager.getPubSubSender().async().publish("PUNISHMENT", redisManager.getGSON().toJson(info));
    }

    public String getIP(String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT IP FROM userdata WHERE UUID = ?");
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("IP");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public String getUUID(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT UUID FROM userdata WHERE name = ?");
            statement.setString(1, name.toLowerCase());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("UUID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public String getDisplay(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT display FROM userdata WHERE UUID = ?");
            statement.setString(1, getUUID(name));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("display");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void updateData(User user) {
        updateData(user.getPlayer().getName(),
                user.getPlayer().getAddress().getAddress().getHostAddress(),
                user.getPlayer().getUniqueId().toString(),
                PlayerUtil.getColoredName(user.getPlayer())
        );
    }

    public void updateData(String name, String ip, String uuid, String display) {
        try {
            PreparedStatement statement;
            if (hasData(name)) {
                statement = connection.prepareStatement("UPDATE userdata SET `name` = ?, `IP` = ?, `display` = ? WHERE UUID = ?");
                statement.setString(1, name.toLowerCase());
                statement.setString(2, ip);
                statement.setString(3, display.replace("§", "&"));
                statement.setString(4, uuid);
                statement.executeUpdate();
            } else {
                statement = connection.prepareStatement("INSERT INTO userdata(`UUID`, `IP`, `name`, `display`) VALUES(?, ?, ?, ?)");
                statement.setString(1, uuid);
                statement.setString(2, ip);
                statement.setString(3, name.toLowerCase());
                statement.setString(4, display.replace("§", "&"));
            }
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasData(String name) {
        return !getUUID(name.toLowerCase()).equals("Unknown");
    }

    public List<BanInfo> getHistory(String uuid) {
        List<BanInfo> bans = Lists.newArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT executor, removed, reason, expiration, type FROM punishments WHERE UUID = ?");
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (!result.getString("type").equals("MUTE")) {
                    bans.add(new BanInfo(
                            result.getString("executor"), result.getString("reason"), result.getLong("expiration"),
                            !result.getString("removed").equals("0"), result.getString("type").equals("IPBAN")
                    ));
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bans;
    }

    public BanInfo getBanInfo(String name) {
        if (!hasData(name)) return null;

        try {
            if (isIPBanned(getIP(getUUID(name)))) {
                PreparedStatement statement = connection.prepareStatement("SELECT executor, reason, expiration FROM punishments WHERE IP = ? AND removed = \"0\"");
                statement.setString(1, getIP(getUUID(name)));
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new BanInfo(result.getString("executor"), result.getString("reason"), result.getLong("expiration"), false, true);
                }
                statement.close();
            } else if (isPunished(name, Punishment.BAN)) {
                PreparedStatement statement = connection.prepareStatement("SELECT executor, reason, expiration FROM punishments WHERE UUID = ? AND removed = \"0\"");
                statement.setString(1, getUUID(name));
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new BanInfo(result.getString("executor"), result.getString("reason"), result.getLong("expiration"), false, false);
                }
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAccounts(String ip) {
        List<String> alts = Lists.newArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT display FROM userdata WHERE IP = ?");
            statement.setString(1, ip);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                alts.add(ChatUtil.color(result.getString("display")));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alts;
    }
}
