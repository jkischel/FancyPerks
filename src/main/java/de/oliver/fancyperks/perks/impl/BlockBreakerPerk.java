package de.oliver.fancyperks.perks.impl;

import org.bukkit.inventory.ItemStack;

import de.oliver.fancyperks.perks.Perk;

public class BlockBreakerPerk extends Perk {

    private boolean blockMiningSupportedBlocks;

    public BlockBreakerPerk(String systemName, String name, String description, ItemStack displayItem) {
        super(systemName, name, description, displayItem);
    }

    public boolean getBlockMiningSupportedBlocks() {
        return blockMiningSupportedBlocks;
    }

    public void setBlockMiningSupportedBlocks(boolean flag) {
        blockMiningSupportedBlocks = flag;
    }

}
