package com.aidanmurphey.usermanager;

import com.aidanmurphey.usermanager.listeners.ChatListener;
import com.aidanmurphey.usermanager.listeners.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class UserManager extends JavaPlugin {
    private static UserManager plugin;
    private static ArrayList<UMPlayer> registeredPlayers;
    private static HashMap<UUID, PermissionAttachment> attachments;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = getDescription();
        getLogger().info("Attempting to initialize " + pdfFile.getName() + ", version: " + pdfFile.getVersion() + "...");

        saveDefaultConfig();
        plugin = this;

        //if registeredPlayers doesn't already exist, create it
        //this check exists so the list doesn't empty itself when an owner *stupidly* reloads the server
        registeredPlayers = registeredPlayers != null ? registeredPlayers : new ArrayList<>();
        attachments = attachments != null ? attachments : new HashMap<>();

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
     * Returns a player's PermissionAttachment
     * @param uuid The player's UUID
     * @return PermissionAttachment assigned to user by this plugin
     */
    public static PermissionAttachment getAttachment(UUID uuid) {
        return attachments.get(uuid);
    }

    /**
     * Attaches a new permission attachment to a user if none already exists
     * @param uuid The player's UUID
     * @param permissionAttachment The new permission attachment for the user
     */
    public static void addAttachment(UUID uuid, PermissionAttachment permissionAttachment) {
        attachments.putIfAbsent(uuid, permissionAttachment);
    }

    /**
     * Unsets an attachment assigned to user by this plugin
     * @param uuid The player's UUID
     */
    public static void removeAttachment(UUID uuid) {
        attachments.remove(uuid);
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
            sender.sendMessage(ChatColor.GREEN + "You've successfully used the command!");
        }
        return true;
    }
}
