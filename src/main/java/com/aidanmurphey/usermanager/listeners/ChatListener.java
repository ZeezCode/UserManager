package com.aidanmurphey.usermanager.listeners;

import com.aidanmurphey.usermanager.Group;
import com.aidanmurphey.usermanager.UMPlayer;
import com.aidanmurphey.usermanager.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    /**
     * Chat listener, used to modify format of messages in chat
     * @param e AsyncPlayerChatEvent
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String chatFormat = UserManager.getPlugin().getConfig().getString("chat.format");

        Player p = e.getPlayer();
        Group group = UMPlayer.getPlayer(p.getUniqueId(), false).getGroup();
        String nameFormat = ChatColor.translateAlternateColorCodes('&', group.getPrefix() + p.getName() + group.getSuffix());

        chatFormat = chatFormat
                .replaceAll("%USER_NAME%", nameFormat)
                .replaceAll("%MESSAGE%", e.getMessage());

        e.setFormat(chatFormat);
    }
}
