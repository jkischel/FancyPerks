package de.oliver.fancyperks.commands;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.gui.customInventories.PerksInventory;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;

public class PerksCMD implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("activate", "deactivate")
                    .filter(input -> input.startsWith(args[0].toLowerCase()))
                    .toList();
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("activate")) {
                List<Perk> enabledPerks = FancyPerks.getInstance().getPerkManager().getEnabledPerks((Player) sender);
                return Stream.concat(
                                Stream.of("*"),
                                PerkRegistry.ALL_PERKS.stream()
                                        .filter(perk -> !enabledPerks.contains(perk))
                                        .map(Perk::getSystemName))
                        .filter(input -> input.startsWith(args[1].toLowerCase()))
                        .toList();
            } else if (args[0].equalsIgnoreCase("deactivate")) {
                List<Perk> enabledPerks = FancyPerks.getInstance().getPerkManager().getEnabledPerks((Player) sender);
                return Stream.concat(
                                Stream.of("*"),
                                PerkRegistry.ALL_PERKS.stream()
                                        .filter(enabledPerks::contains)
                                        .map(Perk::getSystemName))
                        .filter(input -> input.startsWith(args[1].toLowerCase()))
                        .toList();
            }

            return PerkRegistry.ALL_PERKS.stream()
                    .map(Perk::getSystemName)
                    .filter(input -> input.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    Player p = (Player) sender;

    if (args.length == 0) {
        PerksInventory perksInventory = new PerksInventory(p);
        p.openInventory(perksInventory.getInventory());
        return true;
    }

    if (args.length >= 2) {
        String subCommand = args[0].toLowerCase();
        String perkName = args[1];

        switch (subCommand) {
            case "activate" -> {
                return FancyPerks.getInstance().getPerkManager().handlePerkActivation(p, perkName);
            }
            case "deactivate" -> {
                return FancyPerks.getInstance().getPerkManager().handlePerkDeactivation(p, perkName);
            }
        }
    }
        return false;
    }


}
