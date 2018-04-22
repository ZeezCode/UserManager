package com.aidanmurphey.usermanager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Date;
import java.util.UUID;

public class UMPlayer {
    private OfflinePlayer player;
    private Group group;
    private double balance;
    private long playtime, firstSeen, lastSeen; //playtime stored in seconds

    /**
     * Creates an instance of the UMPlayer class
     * @param uuid UUID of player
     * @param group Group of player
     * @param balance Balance of player
     * @param playtime Playtime of player
     * @param firstSeen Timestamp of user first being seen
     * @param lastSeen Timestamp of user last being seen
     */
    protected UMPlayer(UUID uuid, Group group, double balance, long playtime, long firstSeen, long lastSeen) {
        this.player = Bukkit.getOfflinePlayer(uuid);
        this.group = group;
        this.balance = balance;
        this.playtime = playtime;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
    }

    /**
     * Returns the user's UUID
     * @return UUID The UUID of the player
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * Returns the player's OfflinePlayer instance
     * @return OfflinePlayer The user's OfflinePlayer instance
     */
    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    /**
     * Returns the group of the user
     * @return Group Group of user
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Assigns a user to a new group
     * @param group The user's new group
     * @return UMPlayer Current instance of UMPlayer
     */
    public UMPlayer setGroup(Group group) {
        this.group = group;

        return this;
    }

    /**
     * Returns the balance of the player
     * @return double Balance of the user
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the user's balance
     * @param balance The new balance of the user
     * @return UMPlayer Current instance of UMPlayer
     */
    public UMPlayer setBalance(double balance) {
        this.balance = balance;

        return this;
    }

    /**
     * Returns the time, in seconds, the user has spent on the server
     * @return long User's playtime on the server (in seconds)
     */
    public long getPlaytime() {
        return playtime;
    }

    /**
     * Sets the user's playtime
     * @param playtime The user's new playtime
     * @return UMPlayer The current instance of UMPlayer
     */
    private UMPlayer setPlayTime(long playtime) {
        this.playtime = playtime;

        return this;
    }

    /**
     * Returns the UNIX timestamp of when user was first seen (first time on server)
     * @return long Timestamp of user first being seen
     */
    public long getFirstSeen() {
        return firstSeen;
    }

    /**
     * Returns the UNIX timestamp of when user was last seen
     * @return long Timestamp of user last being seen
     */
    public long getLastSeen() {
        return lastSeen;
    }

    /**
     * Sets the time the user was last seen
     * @param lastSeen The timestamp of when the user was last seen
     * @return UMPlayer The current instance of UMPlayer
     */
    public UMPlayer setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;

        return this;
    }

    /**
     * Save's the current state of the UMPlayer to the database
     */
    public void save() {
        DatabaseHandler.savePlayerData(this);
    }

    /**
     * Ran automatically when a user disconnects from the server
     * Used to gather final data before user disconnects
     */
    public void handleDisconnect() {
        UserManager.getRegisteredPlayers().remove(this);

        long timestamp = new Date().getTime() / 1000;
        long newPlayTime = timestamp - lastSeen;

        setLastSeen(timestamp).setPlayTime(getPlaytime() + newPlayTime).save();
    }

    /**
     * Gets an instance of the UMPlayer object for a specific player
     * @param uuid UUID of player
     * @return UMPlayer Instance of UNPlayer for requested user
     */
    public static UMPlayer getPlayer(UUID uuid) {
        //if UMPlayer is online (they'll be in the registeredPlayers list)
        return UserManager.getRegisteredPlayers().stream().filter(
                player -> player.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }
}
