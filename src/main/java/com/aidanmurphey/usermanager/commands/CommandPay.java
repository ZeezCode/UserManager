package com.aidanmurphey.usermanager.commands;

import com.aidanmurphey.usermanager.UMPlayer;
import com.aidanmurphey.usermanager.UserManager;
import com.aidanmurphey.usermanager.Utilities;
import com.aidanmurphey.usermanager.exceptions.CommandFailedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandPay implements UMCommand {

    private List<String> aliases;

    public CommandPay(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandFailedException {
        //if sender isn't a player
        if (!(sender instanceof Player))
            throw new CommandFailedException(UMLanguage.ERROR_MUST_BE_PLAYER);
        Player p = (Player) sender;

        //if not enough args
        if (args.length < 2)
            throw new CommandFailedException(UMLanguage.ERROR_INCORRECT_USAGE + "/pay <player> <amount>");

        Player target = Bukkit.getPlayer(args[0]);
        //if target doesn't exit
        if (target == null)
            throw new CommandFailedException(UMLanguage.ERROR_PLAYER_NOT_FOUND);

        //if amount specified isn't valid number
        if (!Utilities.isValidDouble(args[1]))
            throw new CommandFailedException(UMLanguage.ERROR_INCORRECT_USAGE + "/pay <player> <amount>");
        double amount = Double.parseDouble(args[1]);

        if (amount <= 0)
            throw new CommandFailedException(ChatColor.RED + "The amount cannot be negative or 0!");

        UMPlayer umPlayerSender = UMPlayer.getPlayer(p.getUniqueId());
        UMPlayer umPlayerTarget = UMPlayer.getPlayer(target.getUniqueId());

        ConfigurationSection configurationSection = UserManager.getPlugin().getConfig().getConfigurationSection("economy");
        double min = configurationSection.getDouble("minimum-money");
        double max = configurationSection.getDouble("maximum-money");

        //if player doesn't have enough money for transaction
        if (umPlayerSender.getBalance() - amount < min)
            throw new CommandFailedException(UMLanguage.ERROR_INSUFFICIENT_FUNDS);

        //if target has too much money to receive transaction
        if (umPlayerTarget.getBalance() + amount > max)
            throw new CommandFailedException(
                    ChatColor.RED +
                    "You cannot send " + target.getName() + " this money because it will result in them having more than the max allowed money!"
            );

        sender.sendMessage(ChatColor.GREEN + "You've successfully paid " + target.getName() + "!");
        umPlayerSender.withdrawMoney(amount).save();

        target.sendMessage(ChatColor.GREEN + sender.getName() + " has given you money!");
        umPlayerTarget.depositMoney(amount).save();
    }

}
