package com.aidanmurphey.usermanager.listeners;

import com.aidanmurphey.usermanager.UMPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        UMPlayer umPlayer = UMPlayer.getPlayer(p.getUniqueId(), false);

        if (!umPlayer.getGroup().canBuild()) {
            e.setBuild(false);
            e.setCancelled(true);

            p.sendMessage(ChatColor.RED + "You do not have permission to build!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        UMPlayer umPlayer = UMPlayer.getPlayer(p.getUniqueId(), false);

        if (!umPlayer.getGroup().canBuild()) {
            e.setCancelled(true);

            p.sendMessage(ChatColor.RED + "You do not have permission to break blocks!");
        }
    }

}
