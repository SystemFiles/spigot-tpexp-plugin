package ca.sykesdev.tpexp.Commands;

import ca.sykesdev.tpexp.Models.Transaction;
import ca.sykesdev.tpexp.TPExp;
import ca.sykesdev.tpexp.Utils.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ca.sykesdev.tpexp.Utils.Message;

import java.util.HashMap;

/**
 * The default teleport command to a particular set of coordinates
 */
public class TPExpCommandExecutor implements CommandExecutor {

    /**
     * P2P = Player to Player
     * P2S = Player to Self
     */
    public enum RequestType {
        P2P,
        P2S
    }
    private TPExp plugin;
    private HashMap<String, Transaction> transactions = new Data().getTransactions();

    public TPExpCommandExecutor(TPExp pl) {
        this.plugin = pl;
    }

    /**
     * Register the teleport transaction
     * @param player The player for this transaction
     * @param cost The cost of the transaction
     * @param targetLocation The target location to take the player when this transaction is fulfilled
     */
    private void registerTransaction(Player player, int cost, Location targetLocation, RequestType type) {
        // Check for existing transaction
        String uuid = player.getUniqueId().toString();
        if (this.transactions.containsKey(uuid)) {
            this.transactions.remove(uuid);
        }

        // Store transaction in map
        this.transactions.put(uuid, new Transaction(player, cost, targetLocation, type));
    }

    /**
     * Register the teleport transaction (P2P)
     * @param player The player for this transaction
     * @param targetPlayer The player to teleport to
     * @param cost The cost of the transaction
     * @param targetLocation The targetPlayer location to take the player when this transaction is fulfilled
     */
    private void registerTransaction(Player player, int cost, Location targetLocation, RequestType type, Player targetPlayer) {
        // Check for existing transaction
        String uuid = targetPlayer.getUniqueId().toString();
        if (this.transactions.containsKey(uuid)) {
            this.transactions.remove(uuid);
        }

        // Store transaction in map
        this.transactions.put(uuid, new Transaction(player, targetPlayer, cost, targetLocation, type));
    }

    private void fulfillTransaction(String uuid) {
        Transaction transaction = this.transactions.get(uuid);

        if (transaction.getType() == RequestType.P2P) {
            if (transaction.fulfillTransactionP2P()) {
                // Remove transaction from pending
                this.transactions.remove(uuid);
            }
        } else {
            if (transaction.fulfillTransaction()) {
                // Remove transaction from pending
                this.transactions.remove(uuid);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        double costPerBlock = this.plugin.getConfig().getDouble("costPerBlock");
            if (label.equalsIgnoreCase("tpexp")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(Message.formatMessage("Reloading configuration..."));
                    this.plugin.reloadConfig();
                    sender.sendMessage(Message.formatMessage("Reloaded configuration!"));
                } else {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (args.length >= 1) {
                            switch (args[0]) {
                                case "point":
                                    if (args.length == 4) {
                                        try {
                                            double x = Double.parseDouble(args[1]);
                                            double y = Double.parseDouble(args[2]);
                                            double z = Double.parseDouble(args[3]);

                                            // Calculate cost for player
                                            Location sourceLocation = p.getLocation();
                                            Location targetLocation = new Location(p.getWorld(), x, y, z);
                                            double totalCost = sourceLocation.distance(targetLocation) * costPerBlock;

                                            p.sendMessage(Message.formatMessage(ChatColor.translateAlternateColorCodes('&',
                                                    "This teleport to" + ChatColor.GOLD
                                                            + " [" + x + "," + y + "," + z + "] "
                                                            + ChatColor.WHITE + " will cost you &6"
                                                            + Math.round(totalCost)
                                                            + "xp! &fIf you accept enter: &6/tpexp accept&f")));

                                            // Register this transaction
                                            this.registerTransaction(p, (int) totalCost, targetLocation, RequestType.P2S);
                                            return true;
                                        } catch (NumberFormatException e) {
                                            p.sendMessage(Message.formatMessage(ChatColor.translateAlternateColorCodes('&',
                                                    "&4Problem parsing arguments...please see usage details..")));
                                        }
                                    } else {
                                        p.sendMessage(Message.formatMessage(ChatColor.RED + "Invalid number of arguments...see usage"));
                                    }
                                    break;
                                case "player":
                                    if (args.length == 2) {
                                        Player targetPlayer = Bukkit.getPlayer(args[1]);
                                        if (targetPlayer != null && targetPlayer.isOnline() && targetPlayer != p) {
                                            p.sendMessage(Message.formatMessage("Sending teleport request to " + ChatColor.GOLD
                                                    + targetPlayer.getDisplayName()));

                                            // Calculate cost and get locations
                                            Location sourceLocation = p.getLocation();
                                            Location targetLocation = targetPlayer.getLocation();
                                            double totalCost = sourceLocation.distance(targetLocation) * costPerBlock;

                                            p.sendMessage(Message.formatMessage("This teleport request will cost "
                                                    + ChatColor.GOLD + Math.round(totalCost) + "xp!"));

                                            // Create the transaction (p2p target incl.)
                                            this.registerTransaction(p, (int) totalCost, targetLocation, RequestType.P2P,
                                                    targetPlayer);

                                            targetPlayer.sendMessage(Message.formatMessage(ChatColor.WHITE +
                                                    "You have a teleport request from " + ChatColor.WHITE
                                                    + p.getDisplayName() + ChatColor.WHITE + " type " + ChatColor.GOLD
                                                    + "/tpexp accept" + ChatColor.WHITE + " to accept the request... or "
                                                    + ChatColor.GOLD + "/tpexp deny " + ChatColor.WHITE + " to deny"));
                                        } else if (targetPlayer == p) {
                                            p.sendMessage(Message.formatMessage(ChatColor.RED
                                                    + "You can't request to teleport to yourself!"));
                                        } else {
                                            p.sendMessage(Message.formatMessage(ChatColor.RED
                                                    + "Invalid player specification...are they online?"));
                                        }
                                    } else {
                                        p.sendMessage(Message.formatMessage(ChatColor.RED + "Invalid number of arguments...see usage"));
                                    }
                                    break;
                                case "accept":
                                    if (this.transactions.containsKey(p.getUniqueId().toString())) {
                                        if (this.transactions.get(p.getUniqueId().toString()).getType() == RequestType.P2P) {
                                            // This is a player-to-player request... must be accepted by target player
                                            this.fulfillTransaction(p.getUniqueId().toString());
                                        } else {
                                            this.fulfillTransaction(p.getUniqueId().toString());
                                        }
                                    } else {
                                        p.sendMessage(Message.formatMessage("No transactions pending..."));
                                    }

                                    break;
                                case "deny":
                                    if (this.transactions.containsKey(p.getUniqueId().toString())) {
                                        if (this.transactions.get(p.getUniqueId().toString()).getType() == RequestType.P2P) {
                                            // This is a player-to-player request... must be denied by target player
                                            this.transactions.get(p.getUniqueId().toString()).getPlayer().
                                                    sendMessage(Message.formatMessage(
                                                            "Your Teleport request to " + ChatColor.GOLD
                                                                    + p.getDisplayName() + " was denied..."));
                                        }

                                        p.sendMessage(Message.formatMessage(ChatColor.BOLD + "Teleport request denied.."));
                                        this.transactions.remove(p.getUniqueId().toString());
                                    } else {
                                        p.sendMessage(Message.formatMessage("No transactions pending..."));
                                    }
                                    break;
                                default:
                                    p.sendMessage(Message.formatMessage(ChatColor.RED + "Invalid usage...see usage details"));
                            }
                        } else {
                            p.sendMessage(Message.formatMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&fNo Arguments supplied...")));
                            return false;
                        }
                    } else {
                        sender.sendMessage(Message.formatMessage("This command can only be sent by players..."));
                    }
                }
            } else {
                sender.sendMessage(Message.formatMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4Couldn't understand your command")));
            }

        return true;
    }

}
