package de.oliver.fancyperks.listeners;

import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.PerkManager;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import org.bukkit.Material;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();

        if (event.getBlock().hasMetadata("LavaRunner") && event.getBlock().getType() == Material.OBSIDIAN){
            event.setCancelled(true);
            event.getBlock().setType(Material.OBSIDIAN);
            return;
        }

        PerkManager perkManager = FancyPerks.getInstance().getPerkManager();
        List<Perk> perks = perkManager.getEnabledPerks(p);

        boolean hasAutoPlanting = perks.contains(PerkRegistry.AUTO_PLANTING);
        if (hasAutoPlanting) {
            Block block = event.getBlock();
            BlockData blockData = block.getState().getBlockData();
            if (blockData instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {
                FancyPerks.getInstance().getFancyScheduler().runTaskLater(block.getLocation(), 3L, () -> {
                    block.setType(block.getType());
                    ageable.setAge(0);
                    block.setBlockData(ageable);
                });
            }
        }

        boolean hasDropMoreBlocks = perks.contains(PerkRegistry.DROP_MORE_BLOCKS);
        if (hasDropMoreBlocks) {
            Block block = event.getBlock();
            Material type = block.getType();

            if (type == Material.BUDDING_AMETHYST
             || type == Material.REINFORCED_DEEPSLATE
             || type == Material.DIRT_PATH
             || type == Material.FARMLAND
             || type == Material.FROGSPAWN) {
                event.setDropItems(false);
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(type));

                ItemStack dropItem = new ItemStack(type);
                boolean hasTelekinesis = perks.contains(PerkRegistry.TELEKINESIS);
                if (p.getGameMode() == GameMode.SURVIVAL) {
                    if (hasTelekinesis) {
                            event.setCancelled(true);
                            HashMap<Integer, ItemStack> couldNotFit = p.getInventory().addItem(dropItem);
                            couldNotFit.values().forEach(item ->
                                block.getWorld().dropItemNaturally(block.getLocation(), item)
                            );
                            couldNotFit.values().forEach(item -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item));
                    } else {
                        block.getWorld().dropItemNaturally(block.getLocation(), dropItem);
                    }
                }
             }
        }

        boolean hasDropSpawners = perks.contains(PerkRegistry.DROP_SPAWNERS);
        if (hasDropSpawners) {
            Block block = event.getBlock();
            Material type = block.getType();

            if (type == Material.SPAWNER
            || type == Material.TRIAL_SPAWNER) {
                event.setDropItems(false);

                ItemStack dropItem = new ItemStack(type);
                boolean hasTelekinesis = perks.contains(PerkRegistry.TELEKINESIS);

                if (p.getGameMode() == GameMode.SURVIVAL) {
                    if (hasTelekinesis) {
                        event.setCancelled(true);
                        HashMap<Integer, ItemStack> couldNotFit = p.getInventory().addItem(dropItem);
                        couldNotFit.values().forEach(item ->
                            block.getWorld().dropItemNaturally(block.getLocation(), item)
                        );
                        couldNotFit.values().forEach(item -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item));
                    } else {
                        block.getWorld().dropItemNaturally(block.getLocation(), dropItem);
                    }
                }
            }
        }

    }

}
