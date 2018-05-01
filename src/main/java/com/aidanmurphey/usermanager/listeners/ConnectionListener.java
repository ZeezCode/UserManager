package com.aidanmurphey.usermanager.listeners;

import com.aidanmurphey.usermanager.DatabaseHandler;
import com.aidanmurphey.usermanager.Group;
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
import org.bukkit.permissions.PermissionAttachment;

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
        } else //if player has joined the server before now
            umPlayer.setLastSeen(new Date().getTime() / 1000).save();

        //Add UMPlayer to registered players list
        UserManager.getRegisteredPlayers().add(umPlayer);

        //Setup player's permissions
        PermissionAttachment attachment = p.addAttachment(UserManager.getPlugin());
        for (String permission : Group.getPermissions(umPlayer.getGroup())) {
            if (permission.charAt(0) != '-') //permission doesn't start with - (add permission to user)
                attachment.setPermission(permission, true);
            else //permission starts with - (add new negative permission) to user
                attachment.setPermission(permission.substring(1), false);
        }
        UserManager.addAttachment(p.getUniqueId(), attachment);
    }

    /**
     * Ran when a player leaves the server
     * @param e PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        //Save player's final data before they leave
        UMPlayer.getPlayer(p.getUniqueId(), false).handleDisconnect();

        //Remove player's attachment from local list
        UserManager.removeAttachment(p.getUniqueId());
    }

}
