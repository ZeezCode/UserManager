package com.aidanmurphey.usermanager.listeners;

import com.aidanmurphey.usermanager.DatabaseHandler;
import com.aidanmurphey.usermanager.UMPlayer;
import com.aidanmurphey.usermanager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

public class ConnectionListener implements Listener {

    /**
     * Ran when a player joins the server
     * @param e PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        UMPlayer umPlayer = DatabaseHandler.getPlayerData(p.getUniqueId());

        //umPlayer will be null if player hasn't joined server before
        if (umPlayer == null) {
            umPlayer = DatabaseHandler.registerPlayer(p);
            FileConfiguration fileConfiguration = UserManager.getPlugin().getConfig();

            //if should show alert for user's first join
            if (fileConfiguration.getBoolean("extra.should-show-first-join-alert")) {
                String alertMsg = fileConfiguration.getString("extra.first-join-alert-message")
                        .replaceAll("%USER_NAME%", p.getName());
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', alertMsg));
            }
        } else
            umPlayer.setLastSeen(new Date().getTime() / 1000).save();

        //Add UMPlayer to registered players list
        UserManager.getRegisteredPlayers().add(umPlayer);
    }

    /**
     * Ran when a player leaves the server
     * @param e PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        UMPlayer.getPlayer(e.getPlayer().getUniqueId()).handleDisconnect();
    }

}
