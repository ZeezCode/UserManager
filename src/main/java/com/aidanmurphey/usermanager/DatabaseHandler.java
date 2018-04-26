package com.aidanmurphey.usermanager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Date;

import java.sql.*;
import java.util.UUID;

public class DatabaseHandler {

    private static Connection connection;

    /**
     * Opens the plugin's connection to its database
     */
    private static synchronized void openConnection() {
        boolean shouldOpen = false;
        if (connection == null) shouldOpen = true;
        if (connection != null)
            try {
                if (connection.isClosed()) shouldOpen = true;
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        if (shouldOpen) {
            ConfigurationSection configurationSection = UserManager.getPlugin().getConfig().getConfigurationSection("database");
            String host = configurationSection.getString("host"),
                    port = configurationSection.getString("port"),
                    name = configurationSection.getString("name"),
                    user = configurationSection.getString("user"),
                    pass = configurationSection.getString("pass");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false";
            try {
                connection = DriverManager.getConnection(url, user, pass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the plugin's connection to its database
     */
    private static synchronized void closeConnection() {
        try {
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a user's stored data in a UMPlayer instance or null if they don't exist in our records
     * @param uuid The UUID of the requested player
     * @return UMPlayer The database's information on the player in UMPlayer form
     */
    public static UMPlayer getPlayerData(UUID uuid) {
        UMPlayer umPlayer = null;
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?;");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (result.next()) {
                umPlayer = new UMPlayer(
                        uuid,
                        Group.getGroup(result.getString("group")),
                        result.getDouble("balance"),
                        result.getLong("playtime"),
                        result.getLong("first_seen"),
                        result.getLong("last_seen")
                );
            }
            result.close();
            sql.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
             closeConnection();
        }
        return umPlayer;
    }

    /**
     * Saves a current state of UMPlayer to the database
     * @param umPlayer The player's information to be saved
     */
    public static void savePlayerData(UMPlayer umPlayer) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement(
                    "UPDATE users SET users.group = ?, users.balance = ?, users.playtime = ?, users.first_seen = ?, users.last_seen = ? WHERE users.uuid = ?;"
            );
            sql.setString(1, umPlayer.getGroup().getName());
            sql.setDouble(2, umPlayer.getBalance());
            sql.setLong(3, umPlayer.getPlaytime());
            sql.setLong(4, umPlayer.getFirstSeen());
            sql.setLong(5, umPlayer.getLastSeen());
            sql.setString(6, umPlayer.getUniqueId().toString());

            sql.executeUpdate();
            sql.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Registers a new player to the database and returns an equivalent UMPlayer instance
     * @param p The new player
     * @return UMPlayer The newly registered player's UMPlayer instance
     */
    public static UMPlayer registerPlayer(Player p) {
        UMPlayer umPlayer = null;
        openConnection();
        try {
            Group defGroup = Group.getDefaultGroup();
            double startingBalance = UserManager.getPlugin().getConfig().getDouble("economy.starting-balance");
            long timestamp = new Date().getTime() / 1000;

            PreparedStatement sql = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?);");
            sql.setString(1, p.getUniqueId().toString());
            sql.setString(2, defGroup.getName());
            sql.setDouble(3, startingBalance);
            sql.setLong(4, 0L); //playtime
            sql.setLong(5, timestamp); //first seen
            sql.setLong(6, timestamp); //last seen

            sql.execute();
            sql.close();

            umPlayer = new UMPlayer(
                    p.getUniqueId(),
                    defGroup,
                    startingBalance,
                    0L,
                    timestamp,
                    timestamp
            );
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
             closeConnection();
        }
        return umPlayer;
    }
}
