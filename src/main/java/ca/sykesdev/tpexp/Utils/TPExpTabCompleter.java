package ca.sykesdev.tpexp.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TPExpTabCompleter implements TabCompleter {

    /**
     * Creates auto-complete suggestions
     * @param sender the sender of the command
     * @param command The command being sent
     * @param alias Any aliases
     * @param args The arguments being send with the command (will suggest further)
     * @return The list of suggested arguments for next
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> onlinePlayers = new ArrayList<>();
        for (Player p : sender.getServer().getOnlinePlayers()) {
            onlinePlayers.add(p.getDisplayName());
        }

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        // Add level one args (first arg)
        if (args.length == 1) {
            commands.add("point");
            commands.add("player");
            commands.add("accept");
            commands.add("deny");
            commands.add("reload");

            StringUtil.copyPartialMatches(args[0], commands, completions);
        // Add level two args
        } else if (args.length == 2) {
            if (args[0].equals("player")) {
                commands.addAll(onlinePlayers);
            } else if (args[0].equals("point")) {
                commands.add("x");
            }

            StringUtil.copyPartialMatches(args[1], commands, completions);
        // Add level three args
        } else if (args.length == 3) {
            if (args[0].equals("point")) {
                commands.add("y");
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        // Add level four args
        } else if (args.length == 4) {
            if (args[0].equals("point")) {
                commands.add("z");
            }
            StringUtil.copyPartialMatches(args[3], commands, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
