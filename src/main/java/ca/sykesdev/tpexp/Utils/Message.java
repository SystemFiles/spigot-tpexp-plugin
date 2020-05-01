package ca.sykesdev.tpexp.Utils;

import org.bukkit.ChatColor;

public class Message {

    public static String formatMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', "&6[" + " &fTPExp "
                + "&6]&f " + msg);
    }
}
