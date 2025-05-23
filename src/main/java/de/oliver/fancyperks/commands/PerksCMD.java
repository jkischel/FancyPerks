package de.oliver.fancyperks.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.oliver.fancylib.MessageHelper;
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
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("activate")) {
            String perkStr = args[1];

            if (perkStr.equals("*")) {
                List<String> activatedPerks = new ArrayList<>();
                for (Perk perk : PerkRegistry.ALL_PERKS) {
                    if (perk.hasPermission(p) && !FancyPerks.getInstance().getPerkManager().hasPerkEnabled(p, perk)) {
                        if(!perk.grant(p)){
                            MessageHelper.warning(p, "The " + perk.getSystemName() + " perk is disabled in this world");
                            continue;
                        }
                        activatedPerks.add(perk.getDisplayName());
                    }
                }

                MessageHelper.success(p, "Activated all perks (" + String.join(", ", activatedPerks) + ")");
                return true;
            }

            Perk perk = PerkRegistry.getPerkByName(perkStr);

            if (perk == null) {
                MessageHelper.error(p, "Could not find perk with name: '" + perkStr + "'");
                return false;
            }

            if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(p, perk)) {
                MessageHelper.warning(p, "You already activated this perk");
                return true;
            }

            if (!perk.hasPermission(p)) {
                MessageHelper.error(p, "You don't have permission to use this perk");
                return false;
            }

            if(!perk.grant(p)){
                MessageHelper.warning(p, "The " + perk.getSystemName() + " perk is disabled in this world");
                return false;
            }

            MessageHelper.success(p, "Activated the " + perk.getDisplayName() + " perk");

        } else if (args.length >= 2 && args[0].equalsIgnoreCase("deactivate")) {
            String perkStr = args[1];

            if (perkStr.equals("*")) {
                List<String> deactivatedPerks = new ArrayList<>();
                for (Perk perk : PerkRegistry.ALL_PERKS) {
                    if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(p, perk)) {
                        perk.revoke(p);
                        deactivatedPerks.add(perk.getDisplayName());
                    }
                }

                MessageHelper.success(p, "Deactivated all perks (" + String.join(", ", deactivatedPerks) + ")");
                return true;
            }

            Perk perk = PerkRegistry.getPerkByName(perkStr);

            if (perk == null) {
                MessageHelper.error(p, "Could not find perk with name: '" + perkStr + "'");
                return false;
            }

            if (!FancyPerks.getInstance().getPerkManager().hasPerkEnabled(p, perk)) {
                MessageHelper.warning(p, "You already deactivated this perk");
                return true;
            }

            perk.revoke(p);
            MessageHelper.success(p, "Deactivated the " + perk.getDisplayName() + " perk");
        }

        return false;
    }
}
