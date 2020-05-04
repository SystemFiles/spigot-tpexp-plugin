package ca.sykesdev.tpexp.Utils;

import org.bukkit.ChatColor;

public class Message {

    /**
     * A message formatter for this plugin
     * @param msg The message to be send with the formatted prefix
     * @return The formatted message
     */
    public static String formatMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', "&6[" + " &fTPExp "
                + "&6]&f " + msg);
    }
}
