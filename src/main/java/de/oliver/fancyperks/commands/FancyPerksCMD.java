package de.oliver.fancyperks.commands;

import java.util.Arrays;
import java.util.List;
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
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;


public class FancyPerksCMD implements CommandExecutor, TabCompleter {

    @Override
public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (args.length == 1) {
        return Stream.of("version", "reload", "enableperk", "disableperk", "getperksof")
                .filter(input -> input.startsWith(args[0].toLowerCase()))
                .toList();
    }

    // player name
    if (args.length == 2 && List.of("enableperk", "disableperk", "getperksof").contains(args[0].toLowerCase())) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .filter(name -> name != null)
            .distinct()
            .collect(Collectors.toList());
    }

    // perk name
    if (args.length == 3 && List.of("enableperk", "disableperk").contains(args[0].toLowerCase())) {
        return PerkRegistry.ALL_PERKS.stream()
            .map(Perk::getSystemName)
            .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
            .toList();
    }

    return List.of();
}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
            MessageHelper.info(sender, "<i>Checking version, please wait...</i>");
            new Thread(() -> {
                ComparableVersion newestVersion = FancyPerks.getInstance().getVersionFetcher().fetchNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyPerks.getInstance().getDescription().getVersion());
                if (newestVersion == null) {
                    MessageHelper.error(sender, "Could not find latest version");
                } else if (newestVersion.compareTo(currentVersion) > 0) {
                    MessageHelper.warning(sender, "You are using an outdated version of the FancyPerks Plugin");
                    MessageHelper.warning(sender, "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyPerks.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>");
                } else {
                    MessageHelper.success(sender, "You are using the latest version of the FancyPerks Plugin (" + currentVersion + ")");
                }
            }).start();

        } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            FancyPerks.getInstance().getFanyPerksConfig().reload();
            FancyPerks.getInstance().getLanguageConfig().load();
            MessageHelper.success(sender, "Reloaded the config");

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
        }

        return false;
    }

}
