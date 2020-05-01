package ca.sykesdev.tpexp.Models;

import ca.sykesdev.tpexp.Commands.TPExpCommandExecutor;
import ca.sykesdev.tpexp.Utils.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Transaction {
    private Player player;
    private Player targetPlayer;
    private int cost;
    private Location targetLocation;
    private TPExpCommandExecutor.RequestType type;

    public Transaction(Player player, int cost, Location targetLocation, TPExpCommandExecutor.RequestType type) {
        this.player = player;
        this.cost = cost;
        this.targetLocation = targetLocation;
        this.type = type;
    }

    public Transaction(Player player, Player targetPlayer, int cost, Location targetLocation,
                       TPExpCommandExecutor.RequestType type) {
        this.player = player;
        this.targetPlayer = targetPlayer;
        this.cost = cost;
        this.targetLocation = targetLocation;
        this.type = type;
    }

    private void chargeExp(int currentXP) {
        // Take exp away
        this.player.setTotalExperience(0);
        this.player.setLevel(0);
        this.player.setExp(0);
        this.player.giveExp(currentXP - this.cost);
    }

    /**
     * Fulfill this transaction and teleport the player at cost
     */
    public boolean fulfillTransaction() {
        int currentXP = this.player.getTotalExperience();
        if (currentXP >= this.cost) {

            this.chargeExp(currentXP);

            // Teleport the player
            this.player.teleport(new Location(this.player.getWorld(),
                    this.targetLocation.getX(), this.targetLocation.getY(), this.targetLocation.getZ()));

            this.player.sendMessage(Message.formatMessage("Transaction fulfilled! " + ChatColor.AQUA + "Teleporting..."));
            return true;
        } else {
            this.player.sendMessage(Message.formatMessage(ChatColor.RED + "Not enough XP to fulfill this request..."));
        }

        return false;
    }

    /**
     * Fulfill this transaction and teleport the player at cost
     */
    public boolean fulfillTransactionP2P() {
        int currentXP = this.player.getTotalExperience();
        if (currentXP >= this.cost) {

            this.chargeExp(currentXP);

            // Teleport the player
            this.player.teleport(this.targetPlayer);

            this.player.sendMessage(Message.formatMessage("Transaction fulfilled! "
                    + ChatColor.AQUA + "Teleporting..."));
            this.targetPlayer.sendMessage(Message.formatMessage(this.player.getDisplayName()
                    + " is teleporting to you!"));
            return true;
        } else {
            this.player.sendMessage(Message.formatMessage(ChatColor.RED + "Not enough XP to fulfill this request..."));
            this.targetPlayer.sendMessage(Message.formatMessage(ChatColor.RED + "Sorry "
                    + this.player.getDisplayName() + " does not have enough XP to complete this request..."));
        }

        return false;
    }

    public TPExpCommandExecutor.RequestType getType() {
        return type;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public void setType(TPExpCommandExecutor.RequestType type) {
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }
}
