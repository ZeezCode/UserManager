package com.aidanmurphey.usermanager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class Utilities {

    /**
     * Given a format string, uses amount and config to insert variables to the format and activates color codes
     * @param format String to use as format for message
     * @param amount Amount of money being used in message
     * @return String Fully formatted version of given String
     */
    public static String formatMoney(String format, double amount) {
        ConfigurationSection config = UserManager.getPlugin().getConfig().getConfigurationSection("economy");

        String result = format;
        boolean useSymbol = config.getBoolean("currency-use-symbol");
        if (useSymbol) {
            result = result
                    .replaceAll("%CUR_NAME%", config.getString("currency-symbol"))
                    .replaceAll("%AMOUNT%", Double.toString(amount));
        } else {
            String pluralOrSingleCurrencyName = amount == 1 ? "currency-name" : "currency-name-plural";
            String currencyName = config.getString(pluralOrSingleCurrencyName);

            result = result
                    .replaceAll("%CUR_NAME%", currencyName)
                    .replaceAll("%AMOUNT%", Double.toString(amount));
        }

        return ChatColor.translateAlternateColorCodes('&', result);
    }

}
