package com.aidanmurphey.usermanager.commands;

import com.aidanmurphey.usermanager.UMPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandBalance implements UMCommand {

    private List<String> aliases;

    public CommandBalance(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //balance (player)

        Player target = null;

        if (args.length > 0) { //if specified player
            target = Bukkit.getPlayer(args[0]);

            //if sender didn't request own balance and doesn't have permission to view others' balance
            if (target != sender && !sender.hasPermission("um.balance.others")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return;
            }

            if (target == null) { //if target not found
                sender.sendMessage(ChatColor.RED + "The specified player could not be found!");
                return;
            }
        } else if (sender instanceof Player) { //no args and player used command
            target = (Player) sender;
        } else { //no args and console used command
            sender.sendMessage(ChatColor.RED + "Missing arguments! Use /balance <player>");
            return;
        }

        UMPlayer umPlayer = UMPlayer.getPlayer(target.getUniqueId());
        if (sender == target)
            sender.sendMessage(ChatColor.GREEN + "Balance: " + umPlayer.getBalance());
        else
            sender.sendMessage(ChatColor.GREEN + target.getName() + "'s Balance: " + umPlayer.getBalance());
    }

}
