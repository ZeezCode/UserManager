package com.aidanmurphey.usermanager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utilities {

    /**
     * Given a format string, uses amount and config to insert variables to the format and activates color codes
     * @param format String to use as format for message
     * @param amount Amount of money being used in message
     * @return String Fully formatted version of given String
     */
    public static String formatMoney(String format, double amount) {
        ConfigurationSection config = UserManager.getPlugin().getConfig().getConfigurationSection("economy");

        String result = format != null ? format : "%MONEY%";

        boolean useSymbol = config.getBoolean("currency-use-symbol");
        if (useSymbol) {
            result = result.replace(
                    "%MONEY%",
                    config.getString("currency-symbol") + amount
            );
        } else {
            String pluralOrSingleCurrencyName = amount == 1 ? "currency-name" : "currency-name-plural";
            String currencyName = config.getString(pluralOrSingleCurrencyName);

            result = result.replace(
                    "%MONEY%",
                    amount + " " + currencyName
            );
        }

        return ChatColor.translateAlternateColorCodes('&', result);
    }

    /**
     * Returns whether or not a given string is a valid double
     * @param toParse String to be parsed
     * @return boolean Whether or not the given String is a valid double
     */
    public static boolean isValidDouble(String toParse) {
        try {
            Double.parseDouble(toParse);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Rounds a double to a certain amount of decimal places
     * @param value Number to be rounded
     * @param places Number of decimal places to round to
     * @return double Original double rounded to x decimal places
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
