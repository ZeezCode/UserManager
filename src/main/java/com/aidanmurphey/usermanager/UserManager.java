package com.aidanmurphey.usermanager;

import com.aidanmurphey.usermanager.listeners.ChatListener;
import com.aidanmurphey.usermanager.listeners.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class UserManager extends JavaPlugin {
    private static UserManager plugin;
    private static ArrayList<UMPlayer> registeredPlayers;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = getDescription();
        getLogger().info("Attempting to initialize " + pdfFile.getName() + ", version: " + pdfFile.getVersion() + "...");

        saveDefaultConfig();
        plugin = this;

        //if registeredPlayers doesn't already exist, create it
        //this check exists so the list doesn't empty itself when an owner *stupidly* reloads the server
        registeredPlayers = registeredPlayers != null ? registeredPlayers : new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down " + getDescription().getName() + "...");
    }

    /**
     * Returns a list of all UMPlayers on the server
     * @return ArrayList<UMPlayer> List of UMPlayers on the server
     */
    public static ArrayList<UMPlayer> getRegisteredPlayers() {
        return registeredPlayers;
    }

    /**
     * Returns the running instance of the UserManager plugin class
     * @return UserManager Running instance of UserManager class
     */
    public static UserManager getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("umtest")) {
            sender.sendMessage("Connected players:");
            for (UMPlayer umPlayer : getRegisteredPlayers()) {
                String msg = "Info on: " + umPlayer.getOfflinePlayer().getName() + "\n"
                        + "Group: " + umPlayer.getGroup().getName() + "\n"
                        + "Balance: " + umPlayer.getBalance();
                sender.sendMessage(msg);
            }
        }
        return true;
    }
}
