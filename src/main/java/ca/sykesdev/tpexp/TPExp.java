/**
 *  Source for TPExp
 *
 *  Desc: Costs Exp to teleport to players and points on the map
 */

package ca.sykesdev.tpexp;

import ca.sykesdev.tpexp.Commands.TPExpCommandExecutor;
import ca.sykesdev.tpexp.Utils.TPExpTabCompleter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ca.sykesdev.tpexp.Utils.Message;

public final class TPExp extends JavaPlugin {

    private FileConfiguration config = getConfig();
    private ConsoleCommandSender console;

    @Override
    public void onEnable() {
        this.console = this.getServer().getConsoleSender();
        this.saveDefaultConfig();

        // Add config defaults
        config.addDefault("costPerBlock", 0.15);
        config.options().copyDefaults(true);
        saveConfig();
        this.console.sendMessage(Message.formatMessage("Configuration loaded..."));

        // Add commands to plugin
        try {
            this.getCommand("tpexp").setExecutor(new TPExpCommandExecutor(this));
            this.getCommand("tpexp").setTabCompleter(new TPExpTabCompleter());
        } catch (NullPointerException e) {
            this.console.sendMessage(Message.formatMessage("Issue enabling plugin due to invalid command executor...\n" + e));
            return;
        }

        this.console.sendMessage(Message.formatMessage("Plugin initialized..."));
    }

    @Override
    public void onDisable() {
        this.console.sendMessage(Message.formatMessage("Saving configuration file..."));
        this.saveConfig();
        this.console.sendMessage(Message.formatMessage("Configuration file saved!"));
        this.console.sendMessage(Message.formatMessage("Plugin deactivated successfully..."));
    }
}
