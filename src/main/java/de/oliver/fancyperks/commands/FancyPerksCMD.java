package de.oliver.fancyperks.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.LanguageHelper;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;


public class FancyPerksCMD implements CommandExecutor, TabCompleter {

    @Override
public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (args.length == 1) {
        return Stream.of("version", "reload", "enableperk", "disableperk", "getperksof", "grantperk", "revokeperk", "massgrantperk", "massrevokeperk")
                .filter(input -> input.startsWith(args[0].toLowerCase()))
                .toList();
    }

    // player name
    if (args.length == 2 && List.of("enableperk", "disableperk", "getperksof", "grantperk", "revokeperk").contains(args[0].toLowerCase())) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .filter(name -> name != null)
            .distinct()
            .collect(Collectors.toList());
    }

    // perk name for mass actions
    if (args.length == 2 && List.of("massgrantperk", "massrevokeperk").contains(args[0].toLowerCase()))
    {
        return Stream.concat(
                Stream.of("*"),
                PerkRegistry.ALL_PERKS.stream()
                    .map(Perk::getSystemName)
            )
            .toList();
    }

    // perk name
    if (args.length == 3 && List.of("enableperk", "disableperk", "grantperk", "revokeperk").contains(args[0].toLowerCase()))
    {
        return Stream.concat(
                Stream.of("*"),
                PerkRegistry.ALL_PERKS.stream()
                    .map(Perk::getSystemName)
            )
            .toList();
    }

    return List.of();
}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
            ComparableVersion currentVersion = new ComparableVersion(FancyPerks.getInstance().getDescription().getVersion());
            Map<String, String> replacements = new HashMap<>();
            replacements.put("version", currentVersion.toString());
            String message = LanguageHelper.getLocalizedMessage("version_information", replacements);
            MessageHelper.success(sender, message);

        } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            FancyPerks.getInstance().getFanyPerksConfig().reload();
            FancyPerks.getInstance().getLanguageConfig().load();
            String message = LanguageHelper.getLocalizedMessage("configuration_reloaded", null);
            MessageHelper.success(sender, message);

        } else if (args.length == 3 && args[0].equalsIgnoreCase("enableperk")) {
            String playerName = args[1];
            String perkName = args[2];
            return FancyPerks.getInstance().getPerkManager().activatePerkFor(playerName, perkName, true, sender);

        } else if (args.length >= 1 && args[0].equalsIgnoreCase("disableperk")) {
            String playerName = args[1];
            String perkName = args[2];
            return FancyPerks.getInstance().getPerkManager().deactivatePerkFor(playerName, perkName, true, sender);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("getperksof")) {
            String playerName = args[1];
            FancyPerks.getInstance().getPerkManager().getPlayerPerks(sender, playerName);

        } else if (args.length == 3 && args[0].equalsIgnoreCase("grantperk")) {
            String playerName = args[1];
            String perkName = args[2];
            FancyPerks.getInstance().getPerkManager().grantPerkTo(playerName, perkName, true, sender, false);

        } else if (args.length == 3 && args[0].equalsIgnoreCase("revokeperk")) {
            String playerName = args[1];
            String perkName = args[2];
            FancyPerks.getInstance().getPerkManager().revokePerkFrom(playerName, perkName, true, sender, false);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("massgrantperk")) {
            String perkName = args[1];
            FancyPerks.getInstance().getPerkManager().massGrantPerkAsync(perkName, sender);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("massrevokeperk")) {
            String perkName = args[1];
            FancyPerks.getInstance().getPerkManager().massRevokePerkAsync(perkName, sender);

        }
        
        return false;
    }

}
