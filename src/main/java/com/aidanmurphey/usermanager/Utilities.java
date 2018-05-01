package com.aidanmurphey.usermanager;

import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Utilities {

    /**
     * Returns the amount of money surrounded by the currency name or symbol
     * @param amount Amount of money being used in return String
     * @return String Formatted version of amount
     */
    public static String formatMoney(double amount) {
        ConfigurationSection config = UserManager.getPlugin().getConfig().getConfigurationSection("economy");
        String result;

        boolean useSymbol = config.getBoolean("currency-use-symbol");
        if (!useSymbol) {
            String pluralOrSingleCurrencyName = amount == 1 ? "currency-name" : "currency-name-plural";
            String currencyName = config.getString(pluralOrSingleCurrencyName);

            result = amount + " " + currencyName;
        } else
            result = config.getString("currency-symbol") + amount;

        return result;
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
     * Returns whether or not a given string is a valid UUID
     * @param toParse String to be parsed
     * @return boolean Whether or not the given String is a valid UUID
     */
    public static boolean isValidUUID(String toParse) {
        try {
            UUID.fromString(toParse);
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
