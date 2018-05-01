package com.aidanmurphey.usermanager.commands;

import com.aidanmurphey.usermanager.UMPlayer;
import com.aidanmurphey.usermanager.Utilities;
import com.aidanmurphey.usermanager.exceptions.CommandFailedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandBalance implements UMCommand {

    private String usage;
    private List<String> aliases;

    public CommandBalance(String usage, List<String> aliases) {
        this.usage = usage;
        this.aliases = aliases;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandFailedException {
        Player target = null;

        if (args.length > 0) { //if specified player
            target = Bukkit.getPlayer(args[0]);

            //if sender didn't request own balance and doesn't have permission to view others' balance
            if (target != sender && !sender.hasPermission("um.balance.others"))
                throw new CommandFailedException(UMLanguage.ERROR_NO_PERMISSION);

            //if target not found
            if (target == null)
                throw new CommandFailedException(UMLanguage.ERROR_PLAYER_NOT_FOUND);
        } else if (sender instanceof Player) //no args and player used command
            target = (Player) sender;
        else //no args and console used command
            throw new CommandFailedException(UMLanguage.ERROR_INCORRECT_USAGE);

        UMPlayer umPlayer = UMPlayer.getPlayer(target.getUniqueId(), false);
        String formatted = Utilities.formatMoney(umPlayer.getBalance());
        if (sender == target)
            sender.sendMessage(ChatColor.GREEN + "Balance: " + formatted);
        else
            sender.sendMessage(ChatColor.GREEN + target.getName() + "'s Balance: " + formatted);
    }

}
