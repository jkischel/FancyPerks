package de.oliver.fancyperks.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.PerkManager;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;

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
                event.setExpToDrop(0);

                ItemStack dropItem = new ItemStack(type);

                if (block.getType() == Material.SPAWNER) {
                    BlockState state = block.getState();
                    if (state instanceof CreatureSpawner spawner) {
                        EntityType mobType = spawner.getSpawnedType();

                        if (mobType != null) {
                            ItemMeta meta = dropItem.getItemMeta();
                            if (meta != null) {
                                NamespacedKey key = new NamespacedKey("fancyperks", "spawner_type");
                                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, mobType.name());
                                String mobName = "&4&l" + mobType.name().toUpperCase() + " &6&lSpawner";
                                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mobName));
                                dropItem.setItemMeta(meta);
                            }                        
                        }
                    }
                } 

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
