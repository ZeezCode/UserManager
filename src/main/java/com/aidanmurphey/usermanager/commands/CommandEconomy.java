package com.aidanmurphey.usermanager.commands;

import com.aidanmurphey.usermanager.DatabaseHandler;
import com.aidanmurphey.usermanager.UMPlayer;
import com.aidanmurphey.usermanager.UserManager;
import com.aidanmurphey.usermanager.Utilities;
import com.aidanmurphey.usermanager.exceptions.CommandFailedException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.UUID;

public class CommandEconomy implements UMCommand {

    private String usage;
    private List<String> aliases;

    public CommandEconomy(String usage, List<String> aliases) {
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
        //if not enough args
        if (args.length < 2)
            throw new CommandFailedException(UMLanguage.ERROR_INCORRECT_USAGE);

        String action = args[0].toLowerCase();
        //if action isn't give, set, or reset
        if (!action.equals("give") && !action.equalsIgnoreCase("set") && !action.equals("reset"))
            throw new CommandFailedException(UMLanguage.ERROR_INCORRECT_USAGE);

        //other way of saying if action is give or set
        if (!action.equals("reset") && (args.length < 3 || !Utilities.isValidDouble(args[2])))
            throw new CommandFailedException(UMLanguage.ERROR_INCORRECT_USAGE);

        //if supplied UUID isn't valid UUID
        if (!Utilities.isValidUUID(args[1]))
            throw new CommandFailedException(UMLanguage.ERROR_INCORRECT_USAGE);

        UUID uuid = UUID.fromString(args[1]);
        UMPlayer umPlayer = UMPlayer.getPlayer(uuid);
        if (umPlayer == null) //try to get from umPlayer first so we don't have to query database ^
            umPlayer = DatabaseHandler.getPlayerData(uuid);

        if (umPlayer == null) //if umPlayer is still null even after getting from database, player doesn't exist
            throw new CommandFailedException(UMLanguage.ERROR_PLAYER_NOT_FOUND);

        ConfigurationSection configurationSection = UserManager.getPlugin().getConfig().getConfigurationSection("economy");
        double max = configurationSection.getDouble("maximum-money");
        double min = configurationSection.getDouble("minimum-money");

        if (action.equals("give")) {
            double amount = Double.parseDouble(args[2]);
            boolean give = true;

            //if amount is negative, set give to false and make number positive (so we can withdraw the positive amount)
            if (amount < 0) {
                give = false;
                amount *= -1;
            }

            if (umPlayer.getBalance() + amount > max || umPlayer.getBalance() - amount < min)
                throw new CommandFailedException(ChatColor.RED
                        + "Failed to set money as it will put their balance above the maximum or below the minimum amount!"
                );

            if (give)
                umPlayer.depositMoney(amount).save();
            else
                umPlayer.withdrawMoney(amount).save();

            //using args[2] instead of amount here to preserve the negative sign they may have entered
            sender.sendMessage(ChatColor.GREEN
                    + "You've successfully "
                    + ( give ? "deposited " : "withdrew ")
                    + Utilities.formatMoney(null, amount)
                    + " from the player's account!"
            );
        } else if (action.equals("set") || action.equals("reset")) {
            double amount;
            if (action.equals("set"))
                amount = Double.parseDouble(args[2]);
            else
                amount = configurationSection.getDouble("starting-balance");

            if (amount > max || amount < min)
                throw new CommandFailedException(ChatColor.RED
                        + "Failed to set money as it will put their balance above the maximum or below the minimum amount!"
                );

            //curBalance + x = amount
            //so
            //x = amount - curBalance
            //x is delta
            double delta = amount - umPlayer.getBalance();
            if (delta > 0) //if delta's positive, need to deposit delta for player to have amount balance
                umPlayer.depositMoney(delta).save();
            else //if delta's negative, need to withdraw delta for player to have amount balance
                umPlayer.withdrawMoney(delta * -1).save(); //multiply by -1 b/c can't withdraw negative $

            sender.sendMessage(ChatColor.GREEN
                    + "You have successfully set the player's balance to: "
                    + Utilities.formatMoney(null, amount));
        }
    }

}
