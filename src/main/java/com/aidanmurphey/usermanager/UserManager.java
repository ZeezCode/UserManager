package com.aidanmurphey.usermanager;

import com.aidanmurphey.usermanager.commands.*;
import com.aidanmurphey.usermanager.exceptions.CommandFailedException;
import com.aidanmurphey.usermanager.listeners.ChatListener;
import com.aidanmurphey.usermanager.listeners.ConnectionListener;
import com.aidanmurphey.usermanager.listeners.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserManager extends JavaPlugin {
    private static UserManager plugin;
    private static ArrayList<UMPlayer> registeredPlayers;
    private static ArrayList<UMCommand> commands;
    private static HashMap<UUID, PermissionAttachment> attachments;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = getDescription();
        getLogger().info("Attempting to initialize " + pdfFile.getName() + "...");

        saveDefaultConfig();
        plugin = this;

        if (!DatabaseHandler.setupDatabase()) {
            getLogger().warning("An error occurred during database setup! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        //if lists don't already exist, create them
        //this check exists so the lists don't empty themselves when an owner *stupidly* reloads the server
        registeredPlayers = registeredPlayers != null ? registeredPlayers : new ArrayList<>();
        attachments = attachments != null ? attachments : new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);

        setupCommands();

        getLogger().info(pdfFile.getName() + " has been successfully initialized running version " + pdfFile.getVersion() + "!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down " + getDescription().getName() + "...");
    }

    /**
     * Initializes all of the plugin's command classes and adds them to the local commands list
     */
    private void setupCommands() {
        HashMap<String, Class<?>> classMap = new HashMap<>();
        classMap.put("balance", CommandBalance.class);
        classMap.put("pay", CommandPay.class);
        classMap.put("balancetop", CommandBaltop.class);
        classMap.put("economy", CommandEconomy.class);

        commands = new ArrayList<>();
        for (String commandName : getDescription().getCommands().keySet()) {
            Command command = getCommand(commandName);
            Class<?> commandClass = classMap.get(commandName);
            try {
                List<String> aliases = new ArrayList<>(command.getAliases());
                aliases.add(command.getName());
                //aliases now consists of command's aliases and name

                UMCommand commandExecutor = (UMCommand) commandClass
                        .getConstructor(String.class, List.class)
                        .newInstance(command.getUsage(), aliases);
                commands.add(commandExecutor);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
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

    /**
     * Runs when a command is entered
     * @param sender The sender of the command
     * @param command The command that was ran
     * @param label The name of the command
     * @param args The arguments entered along with the command
     * @return boolean Command success
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, final String label, String[] args) {
        UMCommand umCommand = commands.stream()
                .filter(cmd -> cmd.getAliases().contains(label.toLowerCase()))
                .findAny().orElse(null);

        if (umCommand != null) {
            try {
                umCommand.execute(sender, args);
            } catch(CommandFailedException e) {
                String msg = e.getMessage();
                if (msg.equals(UMLanguage.ERROR_INCORRECT_USAGE))
                    sender.sendMessage(msg + umCommand.getUsage());
                else
                    sender.sendMessage(msg);
            }
        }

        return true;
    }
}
