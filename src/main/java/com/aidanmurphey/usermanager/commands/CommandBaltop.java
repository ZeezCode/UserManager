package com.aidanmurphey.usermanager.commands;

import com.aidanmurphey.usermanager.DatabaseHandler;
import com.aidanmurphey.usermanager.UMPlayer;
import com.aidanmurphey.usermanager.Utilities;
import com.aidanmurphey.usermanager.exceptions.CommandFailedException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CommandBaltop implements UMCommand {

    private List<String> aliases;

    public CommandBaltop(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandFailedException {
        List<UMPlayer> players = DatabaseHandler.getRichest(10);
        if (players == null) //getRichest can return null if an error occurs when working with the database
            throw new CommandFailedException(UMLanguage.ERROR_UNKNOWN);

        sender.sendMessage(ChatColor.GREEN + "The Server's Richest Players:");

        //not using a foreach here b/c I need the indexes
        for (int i = 0; i < players.size(); i++) {
            UMPlayer umPlayer = players.get(i);
            String countPrefix = ChatColor.DARK_GRAY + "" + (i+1) + ") ";
            String nameAndBalance = ChatColor.WHITE + umPlayer.getOfflinePlayer().getName() + " - " + Utilities.formatMoney(null, umPlayer.getBalance());
            sender.sendMessage(countPrefix + nameAndBalance);
        }
    }

}
