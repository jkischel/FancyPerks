package de.oliver.fancyperks.perks.impl;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.oliver.fancyperks.perks.Perk;

public class FlyPerk extends Perk {
    public FlyPerk(String systemName, String name, String description, ItemStack displayItem) {
        super(systemName, name, description, displayItem);
    }

    @Override
    public boolean grant(Player player) {
        if(!super.grant(player)) return false;
        return forceGrant(player);
    }

    @Override
    public boolean forceGrant(Player player) {
        if(!super.grant(player)) return false;

        player.setAllowFlight(true);
        return true;
    }

    @Override
    public void revoke(Player player) {
        super.revoke(player);
        if (player.getGameMode() == GameMode.CREATIVE) return;
        player.setFlying(false);
        player.setAllowFlight(false);
    }
}
